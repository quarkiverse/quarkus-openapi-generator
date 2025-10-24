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

public class GAVCoordinateOpenApiSpecInputProvider implements OpenApiSpecInputProvider {
    private static final Logger LOG = Logger.getLogger(GAVCoordinateOpenApiSpecInputProvider.class);

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
                .filter(rd -> SUPPORTED_EXTENSIONS.contains(rd.getType()))
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
            var gacString = yamlDependency.getKey().toGacString();
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
