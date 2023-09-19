package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getGlobalConfigName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getSanitizedFileName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getSpecConfigName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.API_NAME_SUFFIX;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.BASE_PACKAGE;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.DEFAULT_SECURITY_SCHEME;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.EXCLUDE;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.INCLUDE;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.INPUT_BASE_DIR;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.MODEL_NAME_PREFIX;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.MODEL_NAME_SUFFIX;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.ConfigName.VALIDATE_SPEC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    private static final String CONFIG_KEY_PROPERTY = "config-key";

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

        validateUserConfiguration(context);

        Optional<String> inputBaseDir = getInputBaseDirRelativeToModule(context.inputDir(), context.config());
        final Path openApiDir = inputBaseDir.map(Path::of).orElseGet(context::inputDir);
        final List<String> filesToInclude = context.config().getOptionalValues(getGlobalConfigName(INCLUDE), String.class)
                .orElse(List.of());
        final List<String> filesToExclude = context.config().getOptionalValues(getGlobalConfigName(EXCLUDE), String.class)
                .orElse(List.of());

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
                Path templateDir = context.workDir().resolve("classes").resolve("templates");
                openApiFilesPaths
                        .filter(Files::isRegularFile)
                        .filter(path -> {
                            String fileName = path.getFileName().toString();
                            return fileName.endsWith(inputExtension())
                                    && !filesToExclude.contains(fileName)
                                    && (filesToInclude.isEmpty() || filesToInclude.contains(fileName));
                        })
                        .forEach(openApiFilePath -> generate(context.config(), openApiFilePath, outDir, templateDir,
                                isRestEasyReactive));
            } catch (IOException e) {
                throw new CodeGenException("Failed to generate java files from OpenApi files in " + openApiDir.toAbsolutePath(),
                        e);
            }
            return true;
        }
        return false;
    }

    private static boolean isJacksonReactiveClientPresent(CodeGenContext context) {
        return isExtensionCapabilityPresent(context, Capability.REST_CLIENT_REACTIVE_JACKSON);
    }

    private static boolean isJacksonClassicClientPresent(CodeGenContext context) {
        return isExtensionCapabilityPresent(context, Capability.RESTEASY_JSON_JACKSON_CLIENT);
    }

    private void validateUserConfiguration(CodeGenContext context) throws CodeGenException {
        List<String> configurations = StreamSupport.stream(context.config().getPropertyNames().spliterator(), false)
                .collect(Collectors.toList());
        OpenApiConfigValidator.validateInputConfiguration(configurations);
    }

    private static boolean isExtensionCapabilityPresent(CodeGenContext context, String capability) {
        return context.applicationModel().getExtensionCapabilities().stream()
                .flatMap(extensionCapability -> extensionCapability.getProvidesCapabilities().stream())
                .anyMatch(capability::equals);
    }

    // TODO: do not generate if the output dir has generated files and the openapi file has the same checksum of the previous run
    protected void generate(final Config config, final Path openApiFilePath, final Path outDir,
            Path templateDir, boolean isRestEasyReactive) {
        final String basePackage = getBasePackage(config, openApiFilePath);
        final Boolean verbose = config.getOptionalValue(getGlobalConfigName(CodegenConfig.ConfigName.VERBOSE), Boolean.class)
                .orElse(false);
        final Boolean validateSpec = config.getOptionalValue(getGlobalConfigName(VALIDATE_SPEC), Boolean.class).orElse(true);
        GlobalSettings.setProperty(OpenApiClientGeneratorWrapper.DEFAULT_SECURITY_SCHEME,
                config.getOptionalValue(getGlobalConfigName(DEFAULT_SECURITY_SCHEME), String.class).orElse(""));

        final OpenApiClientGeneratorWrapper generator = createGeneratorWrapper(openApiFilePath, outDir, isRestEasyReactive,
                verbose, validateSpec);

        generator.withTemplateDir(templateDir);

        generator.withClassesCodeGenConfig(ClassCodegenConfigParser.parse(config, basePackage))
                .withCircuitBreakerConfig(CircuitBreakerConfigurationParser.parse(
                        config));

        getApiNameSuffix(config, openApiFilePath)
                .ifPresent(generator::withApiNameSuffix);

        getModelNameSuffix(config, openApiFilePath)
                .ifPresent(generator::withModelNameSuffix);

        getModelNamePrefix(config, openApiFilePath)
                .ifPresent(generator::withModelNamePrefix);

        getValues(config, openApiFilePath, CodegenConfig.ConfigName.MUTINY, Boolean.class)
                .ifPresent(generator::withMutiny);

        getValues(config, openApiFilePath, CodegenConfig.ConfigName.SKIP_FORM_MODEL, String.class)
                .ifPresent(generator::withSkipFormModelConfig);

        getValues(config, openApiFilePath, CodegenConfig.ConfigName.ADDITIONAL_MODEL_TYPE_ANNOTATIONS, String.class)
                .ifPresent(generator::withAdditionalModelTypeAnnotationsConfig);

        getValues(config, openApiFilePath, CodegenConfig.ConfigName.ADDITIONAL_API_TYPE_ANNOTATIONS, String.class)
                .ifPresent(generator::withAdditionalApiTypeAnnotationsConfig);

        getConfigKeyValue(config, openApiFilePath)
                .ifPresentOrElse(generator::withConfigKey,
                        () -> generator.withConfigKey(getSanitizedFileName(openApiFilePath)));

        generator.withReturnResponse(
                getValues(config, openApiFilePath, CodegenConfig.ConfigName.RETURN_RESPONSE, Boolean.class).orElse(false));

        generator.withEnabledSecurityGeneration(
                getValues(config, openApiFilePath, CodegenConfig.ConfigName.ENABLE_SECURITY_GENERATION, Boolean.class)
                        .orElse(true));

        generator.withGeneratePartFilenameConfig(
                getValues(config, openApiFilePath, CodegenConfig.ConfigName.GENERATE_PART_FILENAME, Boolean.class)
                        .orElse(true));

        getValues(config, openApiFilePath, CodegenConfig.ConfigName.PART_FILENAME_VALUE, String.class)
                .ifPresent(generator::withPartFilenameValueConfig);

        generator.withUseFieldNameInPartFilenameConfig(
                getValues(config, openApiFilePath, CodegenConfig.ConfigName.USE_FIELD_NAME_IN_PART_FILENAME,
                        Boolean.class)
                        .orElse(true));

        SmallRyeConfig smallRyeConfig = config.unwrap(SmallRyeConfig.class);

        getValues(smallRyeConfig, openApiFilePath, CodegenConfig.ConfigName.TYPE_MAPPINGS, String.class, String.class)
                .ifPresent(generator::withTypeMappings);

        getValues(smallRyeConfig, openApiFilePath, CodegenConfig.ConfigName.IMPORT_MAPPINGS, String.class, String.class)
                .ifPresent(generator::withImportMappings);

        getValues(smallRyeConfig, openApiFilePath, CodegenConfig.ConfigName.NORMALIZER, String.class, String.class)
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
                .getOptionalValue(getSpecConfigName(BASE_PACKAGE, openApiFilePath), String.class)
                .orElse(String.format("%s.%s", DEFAULT_PACKAGE, getSanitizedFileName(openApiFilePath)));
    }

    private Optional<String> getApiNameSuffix(final Config config, final Path openApiFilePath) {
        return config.getOptionalValue(getSpecConfigName(API_NAME_SUFFIX, openApiFilePath), String.class);
    }

    private Optional<String> getModelNameSuffix(final Config config, final Path openApiFilePath) {
        return config
                .getOptionalValue(getSpecConfigName(MODEL_NAME_SUFFIX, openApiFilePath), String.class);
    }

    private Optional<String> getModelNamePrefix(final Config config, final Path openApiFilePath) {
        return config
                .getOptionalValue(getSpecConfigName(MODEL_NAME_PREFIX, openApiFilePath), String.class);
    }

    private Optional<String> getInputBaseDirRelativeToModule(final Path sourceDir, final Config config) {
        return config.getOptionalValue(getGlobalConfigName(INPUT_BASE_DIR), String.class).map(inputBaseDir -> {
            int srcIndex = sourceDir.toString().lastIndexOf("src");
            return srcIndex < 0 ? null : sourceDir.toString().substring(0, srcIndex) + inputBaseDir;
        });
    }

    private <T> Optional<T> getValues(final Config config, final Path openApiFilePath, CodegenConfig.ConfigName configName,
            Class<T> propertyType) {

        return getConfigKeyValues(config, openApiFilePath, configName, propertyType)
                .or(() -> getValuesBySpecConfigName(config, openApiFilePath, configName, propertyType));
    }

    private <K, V> Optional<Map<K, V>> getValues(final SmallRyeConfig config, final Path openApiFilePath,
            CodegenConfig.ConfigName configName,
            Class<K> kClass, Class<V> vClass) {

        return getConfigKeyValues(config, openApiFilePath, configName, kClass, vClass)
                .or(() -> getValuesBySpecConfigName(config, openApiFilePath, configName, kClass, vClass));
    }

    private static <T> Optional<T> getValuesBySpecConfigName(Config config, Path openApiFilePath,
            CodegenConfig.ConfigName configName,
            Class<T> propertyType) {
        return config
                .getOptionalValue(CodegenConfig.getSpecConfigName(configName, openApiFilePath), propertyType)
                .or(() -> config.getOptionalValue(CodegenConfig.getGlobalConfigName(configName), propertyType));
    }

    private static <K, V> Optional<Map<K, V>> getValuesBySpecConfigName(SmallRyeConfig config, Path openApiFilePath,
            CodegenConfig.ConfigName configName, Class<K> kClass, Class<V> vClass) {
        return config
                .getOptionalValues(CodegenConfig.getSpecConfigName(configName, openApiFilePath), kClass, vClass)
                .or(() -> config.getOptionalValues(CodegenConfig.getGlobalConfigName(configName), kClass, vClass));
    }

    private static <T> Optional<T> getValuesByConfigKey(Config config, String configName, Class<T> propertyType,
            CodegenConfig.ConfigName codegenConfigName) {
        return config
                .getOptionalValue(configName, propertyType)
                .or(() -> config.getOptionalValue(CodegenConfig.getGlobalConfigName(codegenConfigName), propertyType));
    }

    private static <K, V> Optional<Map<K, V>> getValuesByConfigKey(SmallRyeConfig config, CodegenConfig.ConfigName configName,
            Class<K> kClass, Class<V> vClass, String configKey) {
        return config
                .getOptionalValues(CodegenConfig.getSpecConfigNameByConfigKey(configKey, configName), kClass,
                        vClass)
                .or(() -> config.getOptionalValues(CodegenConfig.getGlobalConfigName(configName), kClass, vClass));
    }

    private static Optional<String> getConfigKeyValue(Config config, Path openApiFilePath) {
        String configKey = String.format("quarkus.openapi-generator.codegen.spec.%s.%s", getSanitizedFileName(openApiFilePath),
                CONFIG_KEY_PROPERTY);
        return config.getOptionalValue(configKey, String.class)
                .filter(Predicate.not(String::isBlank));
    }

    private <T> Optional<T> getConfigKeyValues(final Config config, final Path openApiFilePath,
            CodegenConfig.ConfigName configName,
            Class<T> propertyType) {

        Optional<String> possibleConfigKey = getConfigKeyValue(config, openApiFilePath);
        if (possibleConfigKey.isPresent()) {
            return getValuesByConfigKey(config, CodegenConfig.getSpecConfigNameByConfigKey(possibleConfigKey.get(), configName),
                    propertyType, configName);
        }

        return Optional.empty();
    }

    private <K, V> Optional<Map<K, V>> getConfigKeyValues(final SmallRyeConfig config, final Path openApiFilePath,
            CodegenConfig.ConfigName configName,
            Class<K> kClass, Class<V> vClass) {

        Optional<String> possibleConfigKey = getConfigKeyValue(config, openApiFilePath);
        if (possibleConfigKey.isPresent()) {
            return getValuesByConfigKey(config, configName, kClass, vClass, possibleConfigKey.get());
        }

        return Optional.empty();
    }
}
