package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.*;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.*;
import static io.quarkiverse.openapi.generator.deployment.codegen.OpenApiGeneratorCodeGenBase.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;

/**
 * Provides OpenAPI specification input from Maven GAV (GroupId:ArtifactId:Version) coordinates.
 * <p>
 * This provider scans the application's dependencies for YAML or JSON files that match
 * specific criteria and provides them as input for OpenAPI code generation. This provider extends
 * the {@link AbstractGAVCoordinateOpenApiSpecInputProvider} to integrate with the OpenAPI code
 * generation process in Quarkus.
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
 * <li>Includes specific GAVs based on configuration if available otherwise all GAVs are used</li>
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
 * @see AbstractGAVCoordinateOpenApiSpecInputProvider
 * @see SpecInputModel
 * @see CodeGenContext
 */
public class YamlOrJsonGAVCoordinateOpenApiSpecInputProvider extends AbstractGAVCoordinateOpenApiSpecInputProvider {
    @Override
    protected void addInputModels(CodeGenContext context,
            String gacString,
            Path path,
            List<SpecInputModel> inputModels) throws CodeGenException {
        try {
            inputModels.add(new SpecInputModel(gacString, Files.newInputStream(path)));
        } catch (IOException e) {
            throw new CodeGenException("Could not open input stream of %s from %s.".formatted(gacString, path.toString()),
                    e);
        }
    }

    @Override
    protected Set<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }

    @Override
    protected boolean specificGAVSpecInputProviderFilter(final CodeGenContext context, final String gacString) {
        List<String> includeGavs = context.config().getOptionalValues(getGlobalConfigName(INCLUDE_GAVS), String.class)
                .orElse(null);
        if (includeGavs == null) { // default behavior: all GAVs are included
            return true;
        }
        return includeGavs
                .stream().collect(Collectors.toSet())
                .contains(gacString);
    }
}
