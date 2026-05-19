package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.*;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;

/**
 * Provides OpenAPI specification input from Maven GAV (GroupId:ArtifactId:Version) dependencies
 * packaged as JAR or ZIP files.
 * <p>
 * This provider extends the {@link AbstractGAVCoordinateOpenApiSpecInputProvider} and is responsible for
 * scanning application dependencies to identify JAR or ZIP files that contain OpenAPI specifications
 * (e.g., `openapi.yaml`).
 * </p>
 *
 * <h2>Supported File Types</h2>
 * <p>
 * The provider specifically supports dependencies packaged as:
 * </p>
 * <ul>
 * <li>JAR files</li>
 * <li>ZIP files</li>
 * </ul>
 *
 * <h2>Scanning Behavior</h2>
 * <p>
 * The provider performs the following steps:
 * </p>
 * <ol>
 * <li>Checks if GAV scanning is enabled via configuration (enabled by default)</li>
 * <li>Filters dependencies by artifact type (jar/zip)</li>
 * <li>Applies artifact ID filtering using a regex pattern</li>
 * <li>Excludes specific GAVs based on configuration</li>
 * <li>Includes specific GAVs based on configuration if not available no GAVs are used</li>
 * <li>Creates {@link ZippedSpecInputModel} instances for each matching dependency and openAPI specification file</li>
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
 * <li>{@code quarkus.openapi-generator.codegen.gav.com_sample_customer_service_openapi.spec-files} - List of
 * openAPI specification files in com.sample:customer-service-openapi:jar</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 *
 * <pre>
 * # application.properties
 * quarkus.openapi-generator.codegen.gav-scanning=true
 * quarkus.openapi-generator.codegen.artifact-id-filter=.*api.*
 * quarkus.openapi-generator.codegen.exclude-gavs=com.example:old-api
 * quarkus.openapi-generator.codegen.gav.com_sample_customer_service_api.spec-files=customer.yaml,another.yaml
 * </pre>
 *
 * @see AbstractGAVCoordinateOpenApiSpecInputProvider
 * @see ZippedSpecInputModel
 * @see CodeGenContext
 */
public class JarOrZipGAVCoordinateOpenApiSpecInputProvider extends AbstractGAVCoordinateOpenApiSpecInputProvider {
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("jar", "zip");

    @Override
    protected void addInputModels(CodeGenContext context,
            String gacString,
            Path path,
            List<SpecInputModel> inputModels) throws CodeGenException {
        List<String> rootFilesOfSpecOfDependency = context.config()
                .getOptionalValues(getGavConfigName(SPEC_FILES, Paths.get(gacString)), String.class)
                .orElse(List.of("openapi.yaml"));
        for (String rootFileOfSpecForDependency : rootFilesOfSpecOfDependency) {
            try {
                inputModels.add(new ZippedSpecInputModel(
                        gacString,
                        rootFileOfSpecForDependency,
                        Files.newInputStream(path)));
            } catch (IOException e) {
                throw new CodeGenException(
                        "Could not open input stream of %s from %s.".formatted(gacString, path.toString()),
                        e);
            }
        }
    }

    @Override
    protected Set<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }

    @Override
    protected boolean specificGAVSpecInputProviderFilter(final CodeGenContext context, final String gacString) {
        return new HashSet<>(context.config().getOptionalValues(getGlobalConfigName(INCLUDE_GAVS), String.class)
                .orElse(List.of())) // default to empty list to disable all if not specified
                .contains(gacString);
    }
}
