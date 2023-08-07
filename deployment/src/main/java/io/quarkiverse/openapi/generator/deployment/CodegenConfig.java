package io.quarkiverse.openapi.generator.deployment;

import java.nio.file.Path;
import java.util.Map;

import io.quarkiverse.openapi.generator.deployment.codegen.OpenApiGeneratorOutputPaths;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.common.utils.StringUtil;

// This configuration is read in codegen phase (before build time), the annotation is for document purposes and avoiding quarkus warns
@ConfigRoot(name = CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX, phase = ConfigPhase.BUILD_TIME)
public class CodegenConfig {

    static final String CODEGEN_TIME_CONFIG_PREFIX = "openapi-generator.codegen";

    public static final String API_PKG_SUFFIX = ".api";
    public static final String MODEL_PKG_SUFFIX = ".model";
    public static final String VERBOSE_PROPERTY_NAME = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".verbose";
    public static final String INPUT_BASE_DIR = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".input-base-dir";
    public static final String INCLUDE_FILES = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".include";
    public static final String EXCLUDE_FILES = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".exclude";
    public static final String VALIDATE_SPEC_PROPERTY_NAME = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".validateSpec";
    public static final String DEFAULT_SECURITY_SCHEME = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".default.security.scheme";
    // package visibility for unit tests
    static final String BUILD_TIME_GLOBAL_PREFIX_FORMAT = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".%s";
    static final String BUILD_TIME_SPEC_PREFIX_FORMAT = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".spec.%s";

    private static final String BASE_PACKAGE_PROP_FORMAT = "%s.base-package";

    public static final String SKIP_FORM_MODEL = "skip-form-model";
    public static final String MUTINY = "mutiny";
    public static final String ADDITIONAL_MODEL_TYPE_ANNOTATIONS = "additional-model-type-annotations";
    public static final String ADDITIONAL_API_TYPE_ANNOTATIONS = "additional-api-type-annotations";
    public static final String TYPE_MAPPINGS = "type-mappings";
    public static final String IMPORT_MAPPINGS = "import-mappings";
    public static final String NORMALIZER = "open-api-normalizer";

    public static final String RETURN_RESPONSE = "return-response";
    public static final String ENABLE_SECURITY_GENERATION = "enable-security-generation";

    /**
     * OpenAPI Spec details for codegen configuration.
     */
    @ConfigItem(name = "spec")
    public Map<String, SpecItemConfig> specItem;

    /**
     * Whether to log the internal generator codegen process in the default output or not.
     */
    @ConfigItem(name = "verbose", defaultValue = "false")
    public boolean verbose;

    /**
     * Whether or not to skip validating the input spec prior to generation. By default, invalid specifications will result in
     * an error.
     */
    @ConfigItem(name = "validateSpec", defaultValue = "true")
    public boolean validateSpec;
    /**
     * Security type for which security constraints should be created automatically if not explicitly defined
     */
    @ConfigItem(name = "default.security.scheme", defaultValue = "none")
    public String defaultSecurityScheme;

    public static String resolveApiPackage(final String basePackage) {
        return String.format("%s%s", basePackage, API_PKG_SUFFIX);
    }

    public static String resolveModelPackage(final String basePackage) {
        return String.format("%s%s", basePackage, MODEL_PKG_SUFFIX);
    }

    public static String getBasePackagePropertyName(final Path openApiFilePath) {
        return String.format(BASE_PACKAGE_PROP_FORMAT, getBuildTimeSpecPropertyPrefix(openApiFilePath));
    }

    public static String getGlobalConfigName(String configName) {
        return String.format(BUILD_TIME_GLOBAL_PREFIX_FORMAT, configName);
    }

    public static String getApiSpecConfigName(String configName, final Path openApiFilePath) {
        return String.format("%s.%s", getBuildTimeSpecPropertyPrefix(openApiFilePath), configName);
    }

    /**
     * Gets the config prefix for a given OpenAPI file in the path.
     * For example, given a path like /home/luke/projects/petstore.json, the returned value is
     * `quarkus.openapi-generator."petstore_json"`.
     * Every the periods (.) in the file name will be replaced by underscore (_).
     */
    public static String getBuildTimeSpecPropertyPrefix(final Path openApiFilePath) {
        return String.format(BUILD_TIME_SPEC_PREFIX_FORMAT, getSanitizedFileName(openApiFilePath));
    }

    public static String getSanitizedFileName(final Path openApiFilePath) {
        return StringUtil
                .replaceNonAlphanumericByUnderscores(OpenApiGeneratorOutputPaths.getRelativePath(openApiFilePath).toString());
    }
}
