package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.*;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;

import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.maven.dependency.ResolvedDependency;

public class GAVCoordinateOpenApiSpecInputProvider implements OpenApiSpecInputProvider {
    private static final Logger LOG = Logger.getLogger(GAVCoordinateOpenApiSpecInputProvider.class);

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("yaml", "yml", "json");

    @Override
    public List<SpecInputModel> read(CodeGenContext context) throws CodeGenException {
        if (!context.config().getOptionalValue(getGlobalConfigName(GAV_SCANNING), Boolean.class)
                .orElse(true)) {
            LOG.debug("GAV scanning is disabled.");
            return List.of();
        }

        // Q: maybe a configuration property to enable GAV scanning (default: true)
        List<String> gavsToExclude = context.config().getOptionalValues(getGlobalConfigName(EXCLUDE_GAV), String.class)
                .orElse(List.of());

        List<ResolvedDependency> yamlDependencies = context.applicationModel().getDependencies().stream()
                .filter(rd -> SUPPORTED_EXTENSIONS.contains(rd.getType()))
                .filter(rd -> !gavsToExclude.contains(rd.getKey().toGacString()))
                .toList();

        if (yamlDependencies.isEmpty()) {
            LOG.debug("No suitable GAV dependencies found.");
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
