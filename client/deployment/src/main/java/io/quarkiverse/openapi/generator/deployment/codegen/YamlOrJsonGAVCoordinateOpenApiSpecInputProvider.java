package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getGlobalConfigName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.ARTIFACT_ID_FILTER;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.EXCLUDE_GAVS;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.GAV_SCANNING;
import static io.quarkiverse.openapi.generator.deployment.codegen.OpenApiGeneratorCodeGenBase.SUPPORTED_EXTENSIONS;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.jboss.logging.Logger;

import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.smallrye.config.common.utils.StringUtil;

/**
 * Provides OpenAPI specification input from Maven GAV (GroupId:ArtifactId:Version) coordinates.
 * <p>
 * This provider scans the application's dependencies for YAML or JSON files that match
 * specific criteria and provides them as input for OpenAPI code generation. It implements
 * the {@link OpenApiSpecInputProvider} interface to integrate with the OpenAPI Generator's
 * code generation pipeline.
 * </p>
 *
 * <h2>Scanning Behavior</h2>
 * <p>
 * The provider performs the following steps:
 * </p>
 * <ol>
 * <li>Checks if GAV scanning is enabled via configuration (enabled by default)</li>
 * <li>Filters dependencies by artifact type (yaml/yml/json)</li>
 * <li>Applies artifact ID filtering using a regex pattern</li>
 * <li>Excludes specific GAVs based on configuration</li>
 * <li>Creates {@link SpecInputModel} instances for each matching dependency</li>
 * </ol>
 *
 * <h2>Configuration</h2>
 * <p>
 * The provider respects the following configuration properties:
 * </p>
 * <ul>
 * <li>{@code quarkus.openapi-generator.codegen.gav-scanning} - Enable/disable GAV scanning</li>
 * <li>{@code quarkus.openapi-generator.codegen.artifact-id-filter} - Regex pattern for artifact ID filtering</li>
 * <li>{@code quarkus.openapi-generator.codegen.exclude-gavs} - List of GAV coordinates to exclude
 * (format: groupId:artifactId:classifier)</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 *
 * <pre>
 * # application.properties
 * quarkus.openapi-generator.codegen.gav-scanning=true
 * quarkus.openapi-generator.codegen.artifact-id-filter=.*api.*
 * quarkus.openapi-generator.codegen.exclude-gavs=com.example:old-api
 * </pre>
 *
 * @see OpenApiSpecInputProvider
 * @see SpecInputModel
 * @see CodeGenContext
 */
public class YamlOrJsonGAVCoordinateOpenApiSpecInputProvider implements OpenApiSpecInputProvider {
    private static final Logger LOG = Logger.getLogger(YamlOrJsonGAVCoordinateOpenApiSpecInputProvider.class);

    @Override
    public List<SpecInputModel> read(CodeGenContext context) throws CodeGenException {
        if (!context.config().getOptionalValue(getGlobalConfigName(GAV_SCANNING), Boolean.class)
                .orElse(true)) {
            LOG.debug("GAV scanning is disabled.");
            return List.of();
        }

        List<String> gavsToExclude = context.config().getOptionalValues(getGlobalConfigName(EXCLUDE_GAVS), String.class)
                .orElse(List.of());
        String artifactIdFilter = context.config().getOptionalValue(getGlobalConfigName(ARTIFACT_ID_FILTER), String.class)
                .filter(Predicate.not(String::isBlank))
                .orElse(".*openapi.*");

        List<ResolvedDependency> yamlDependencies = context.applicationModel().getDependencies().stream()
                .filter(rd -> SUPPORTED_EXTENSIONS.contains(rd.getType().toLowerCase()))
                .filter(rd -> rd.getArtifactId().matches(artifactIdFilter))
                .filter(rd -> !gavsToExclude.contains(rd.getKey().toGacString()))
                .toList();

        if (yamlDependencies.isEmpty()) {
            LOG.debug("No suitable GAV dependencies found. ArtifactIdFilter was %s and gavsToExclude were %s."
                    .formatted(artifactIdFilter, gavsToExclude));
            return List.of();
        }
        var inputModels = new ArrayList<SpecInputModel>();
        for (ResolvedDependency yamlDependency : yamlDependencies) {
            var gacString = StringUtil.replaceNonAlphanumericByUnderscores(yamlDependency.getKey().toGacString());
            var path = yamlDependency.getResolvedPaths().stream().findFirst()
                    .orElseThrow(() -> new CodeGenException("Could not find maven path of %s.".formatted(gacString)));
            try {
                inputModels.add(new SpecInputModel(gacString, Files.newInputStream(path)));
            } catch (IOException e) {
                throw new CodeGenException("Could not open input stream of %s from %s.".formatted(gacString, path.toString()),
                        e);
            }
        }
        return inputModels;
    }
}
