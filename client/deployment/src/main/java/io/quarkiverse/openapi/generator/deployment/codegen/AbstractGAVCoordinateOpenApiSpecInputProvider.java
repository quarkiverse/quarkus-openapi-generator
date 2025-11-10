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
                .filter(rd -> specificGAVSpecInputProviderFilter(context, rd.getKey().toGacString()))
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

    /**
     * Adds input models to the provided list based on the given context, GAC string, and path.
     * This method is implemented by subclasses to generate or retrieve the appropriate
     * {@code SpecInputModel} instances that will be processed during code generation.
     *
     * @param context the code generation context, providing access to configuration and utilities
     * @param gacString the GAC (Group, Artifact, Classifier) string representing the dependency identifier
     * @param path the path to the file or directory containing the input specification(s)
     * @param inputModels the list to which the generated {@code SpecInputModel} instances are added
     * @throws CodeGenException if an error occurs while processing the input specifications
     */
    protected abstract void addInputModels(CodeGenContext context,
            String gacString,
            Path path,
            List<SpecInputModel> inputModels) throws CodeGenException;

    /**
     * Filters dependencies based on specific criteria defined in the implementing class.
     * This method is invoked as part of the dependency resolution process to determine
     * whether a dependency identified by its GAC string should be included for further processing.
     *
     * @param context the code generation context, providing access to configuration and other utilities
     * @param gacString the GAC (Group, Artifact, Classifier) string representing the dependency identifier
     * @return true if the dependency matches the filter criteria and should be included; false otherwise
     */
    protected abstract boolean specificGAVSpecInputProviderFilter(CodeGenContext context, String gacString);
}
