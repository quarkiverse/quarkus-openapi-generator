package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.EXCLUDE_FILES;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.INCLUDE_FILES;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.INPUT_BASE_DIR;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.VALIDATE_SPEC_PROPERTY_NAME;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.VERBOSE_PROPERTY_NAME;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getAdditionalApiTypeAnnotationsPropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getAdditionalModelTypeAnnotationsPropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getBasePackagePropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getEnabledSecurityGenerationPropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getImportMappingsPropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getMutinyPropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getNormalizerPropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getReturnResponsePropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getSanitizedFileName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getSkipFormModelPropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getTypeMappingsPropertyName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.Config;
import org.openapitools.codegen.config.GlobalSettings;

import io.quarkiverse.openapi.generator.deployment.CodegenConfig;
import io.quarkiverse.openapi.generator.deployment.circuitbreaker.CircuitBreakerConfigurationParser;
import io.quarkiverse.openapi.generator.deployment.wrapper.OpenApiClassicClientGeneratorWrapper;
import io.quarkiverse.openapi.generator.deployment.wrapper.OpenApiClientGeneratorWrapper;
import io.quarkiverse.openapi.generator.deployment.wrapper.OpenApiReactiveClientGeneratorWrapper;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.smallrye.config.SmallRyeConfig;

/**
 * Code generation for OpenApi Client. Generates Java classes from OpenApi spec files located in src/main/openapi or
 * src/test/openapi
 * <p>
 * Wraps the <a href="https://openapi-generator.tech/docs/generators/java">OpenAPI Generator Client for Java</a>
 */
public abstract class OpenApiGeneratorCodeGenBase implements CodeGenProvider {

    static final String YAML = ".yaml";
    static final String YML = ".yml";
    static final String JSON = ".json";

    private static final String DEFAULT_PACKAGE = "org.openapi.quarkus";

    /**
     * The input base directory from
     *
     * <pre>
     * src/main
     *
     * <pre>
     * directory.
     * Ignored if INPUT_BASE_DIR is specified.
     **/
    @Override
    public String inputDirectory() {
        return "openapi";
    }

    @Override
    public boolean shouldRun(Path sourceDir, Config config) {
        String inputBaseDir = getInputBaseDirRelativeToModule(sourceDir, config).orElse(null);

        if (inputBaseDir != null) {
            return Files.isDirectory(Path.of(inputBaseDir));
        } else {
            return Files.isDirectory(sourceDir) || sourceDir.endsWith(Path.of("src", "test", this.inputDirectory()));
        }
    }

    protected boolean isRestEasyReactive(CodeGenContext context) {
        return context.applicationModel().getExtensionCapabilities().stream()
                .flatMap(extensionCapability -> extensionCapability.getProvidesCapabilities().stream())
                .anyMatch(Capability.REST_CLIENT_REACTIVE::equals);
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        final Path outDir = context.outDir();
        Optional<String> inputBaseDir = getInputBaseDirRelativeToModule(context.inputDir(), context.config());
        final Path openApiDir = inputBaseDir.map(Path::of).orElseGet(context::inputDir);
        final List<String> filesToInclude = context.config().getOptionalValues(INCLUDE_FILES, String.class).orElse(List.of());
        final List<String> filesToExclude = context.config().getOptionalValues(EXCLUDE_FILES, String.class).orElse(List.of());

        if (Files.isDirectory(openApiDir)) {
            final boolean isRestEasyReactive = isRestEasyReactive(context);

            if (isRestEasyReactive) {
                if (!isJacksonReactiveClientPresent(context)) {
                    throw new CodeGenException(
                            "You need to add io.quarkus:quarkus-rest-client-reactive-jackson to your dependencies.");
                }
            } else if (!isJacksonClassicClientPresent(context)) {
                throw new CodeGenException("You need to add io.quarkus:quarkus-rest-client-jackson to your dependencies.");
            }

            try (Stream<Path> openApiFilesPaths = Files.walk(openApiDir)) {
                openApiFilesPaths
                        .filter(Files::isRegularFile)
                        .filter(path -> {
                            String fileName = path.getFileName().toString();
                            return fileName.endsWith(inputExtension())
                                    && !filesToExclude.contains(fileName)
                                    && (filesToInclude.isEmpty() || filesToInclude.contains(fileName));
                        })
                        .forEach(openApiFilePath -> generate(context.config(), openApiFilePath, outDir, isRestEasyReactive));
            } catch (IOException e) {
                throw new CodeGenException("Failed to generate java files from OpenApi files in " + openApiDir.toAbsolutePath(),
                        e);
            }
            return true;
        }
        return false;
    }

    private boolean isJacksonReactiveClientPresent(CodeGenContext context) {
        return context.applicationModel().getDependencies().stream()
                .anyMatch(this::isJacksonReactiveClient);
    }

    private boolean isJacksonClassicClientPresent(CodeGenContext context) {
        return context.applicationModel().getExtensionCapabilities().stream()
                .flatMap(extensionCapability -> extensionCapability.getProvidesCapabilities().stream())
                .anyMatch(Capability.RESTEASY_JSON_JACKSON_CLIENT::equals);
    }

    private boolean isJacksonReactiveClient(ResolvedDependency resolvedDependency) {
        return "quarkus-rest-client-reactive-jackson".equals(resolvedDependency.getArtifactId())
                && "io.quarkus".equals(resolvedDependency.getGroupId());
    }

    // TODO: do not generate if the output dir has generated files and the openapi file has the same checksum of the previous run
    protected void generate(final Config config, final Path openApiFilePath, final Path outDir,
            boolean isRestEasyReactive) {
        final String basePackage = getBasePackage(config, openApiFilePath);
        final Boolean verbose = config.getOptionalValue(VERBOSE_PROPERTY_NAME, Boolean.class).orElse(false);
        final Boolean validateSpec = config.getOptionalValue(VALIDATE_SPEC_PROPERTY_NAME, Boolean.class).orElse(true);
        GlobalSettings.setProperty(OpenApiClientGeneratorWrapper.DEFAULT_SECURITY_SCHEME,
                config.getOptionalValue(CodegenConfig.DEFAULT_SECURITY_SCHEME, String.class).orElse(""));

        final OpenApiClientGeneratorWrapper generator = createGeneratorWrapper(openApiFilePath, outDir, isRestEasyReactive,
                verbose, validateSpec);

        generator.withClassesCodeGenConfig(ClassCodegenConfigParser.parse(config, basePackage))
                .withCircuitBreakerConfig(CircuitBreakerConfigurationParser.parse(
                        config));

        config.getOptionalValue(getMutinyPropertyName(openApiFilePath), Boolean.class)
                .ifPresent(generator::withMutiny);

        config.getOptionalValue(getSkipFormModelPropertyName(openApiFilePath), String.class)
                .ifPresent(generator::withSkipFormModelConfig);

        config.getOptionalValue(getAdditionalModelTypeAnnotationsPropertyName(openApiFilePath), String.class)
                .ifPresent(generator::withAdditionalModelTypeAnnotationsConfig);

        config.getOptionalValue(getAdditionalApiTypeAnnotationsPropertyName(openApiFilePath), String.class)
                .ifPresent(generator::withAdditionalApiTypeAnnotationsConfig);

        generator.withReturnResponse(config.getOptionalValue(getReturnResponsePropertyName(openApiFilePath), Boolean.class)
                .orElse(false));

        generator.withEnabledSecurityGeneration(
                config.getOptionalValue(getEnabledSecurityGenerationPropertyName(openApiFilePath), Boolean.class).orElse(true));

        SmallRyeConfig smallRyeConfig = config.unwrap(SmallRyeConfig.class);
        smallRyeConfig.getOptionalValues(getTypeMappingsPropertyName(openApiFilePath), String.class, String.class)
                .ifPresent(generator::withTypeMappings);

        smallRyeConfig.getOptionalValues(getImportMappingsPropertyName(openApiFilePath), String.class, String.class)
                .ifPresent(generator::withImportMappings);

        smallRyeConfig.getOptionalValues(getNormalizerPropertyName(openApiFilePath), String.class, String.class)
                .ifPresent(generator::withOpenApiNormalizer);

        generator.generate(basePackage);
    }

    private static OpenApiClientGeneratorWrapper createGeneratorWrapper(Path openApiFilePath, Path outDir,
            boolean isRestEasyReactive, Boolean verbose, Boolean validateSpec) {
        if (isRestEasyReactive) {
            return new OpenApiReactiveClientGeneratorWrapper(
                    openApiFilePath.normalize(),
                    outDir,
                    verbose,
                    validateSpec);
        } else {
            return new OpenApiClassicClientGeneratorWrapper(
                    openApiFilePath.normalize(),
                    outDir,
                    verbose,
                    validateSpec);
        }
    }

    private String getBasePackage(final Config config, final Path openApiFilePath) {
        return config
                .getOptionalValue(getBasePackagePropertyName(openApiFilePath), String.class)
                .orElse(String.format("%s.%s", DEFAULT_PACKAGE, getSanitizedFileName(openApiFilePath)));
    }

    private Optional<String> getInputBaseDirRelativeToModule(final Path sourceDir, final Config config) {
        return config.getOptionalValue(INPUT_BASE_DIR, String.class).map(inputBaseDir -> {
            int srcIndex = sourceDir.toString().lastIndexOf("src");
            return srcIndex < 0 ? null : sourceDir.toString().substring(0, srcIndex) + inputBaseDir;
        });
    }
}
