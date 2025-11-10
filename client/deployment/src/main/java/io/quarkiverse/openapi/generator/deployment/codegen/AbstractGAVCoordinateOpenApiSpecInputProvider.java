package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.*;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.jboss.logging.Logger;

import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.smallrye.config.common.utils.StringUtil;

abstract class AbstractGAVCoordinateOpenApiSpecInputProvider implements OpenApiSpecInputProvider {
    private static final Logger LOG = Logger.getLogger(AbstractGAVCoordinateOpenApiSpecInputProvider.class);

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

        List<ResolvedDependency> dependencies = context.applicationModel().getDependencies().stream()
                .filter(rd -> getSupportedExtensions().contains(rd.getType().toLowerCase()))
                .filter(rd -> rd.getArtifactId().matches(artifactIdFilter))
                .filter(rd -> !gavsToExclude.contains(rd.getKey().toGacString()))
                .toList();

        if (dependencies.isEmpty()) {
            LOG.debug("No suitable GAV dependencies found. ArtifactIdFilter was %s and gavsToExclude were %s."
                    .formatted(artifactIdFilter, gavsToExclude));
            return List.of();
        }

        var inputModels = new ArrayList<SpecInputModel>();
        for (ResolvedDependency dependency : dependencies) {
            var gacString = StringUtil.replaceNonAlphanumericByUnderscores(dependency.getKey().toGacString());
            var path = dependency.getResolvedPaths().stream().findFirst()
                    .orElseThrow(() -> new CodeGenException("Could not find maven path of %s.".formatted(gacString)));
            addInputModels(context, gacString, path, inputModels);
        }
        return inputModels;
    }

    protected abstract Set<String> getSupportedExtensions();

    protected abstract void addInputModels(CodeGenContext context,
            String gacString,
            Path path,
            List<SpecInputModel> inputModels) throws CodeGenException;
}
