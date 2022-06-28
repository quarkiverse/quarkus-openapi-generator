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
    public static final String DEFAULT_SECURITY_SCHEME = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".default.security.scheme";
    // package visibility for unit tests
    static final String BUILD_TIME_SPEC_PREFIX_FORMAT = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".spec.%s";
    private static final String BASE_PACKAGE_PROP_FORMAT = "%s.base-package";
    private static final String SKIP_FORM_MODEL_PROP_FORMAT = "%s.skip-form-model";
    private static final String ADDITIONAL_MODEL_TYPE_ANNOTATIONS_PROP_FORMAT = "%s.additional-model-type-annotations";

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

    public static String getSkipFormModelPropertyName(final Path openApiFilePath) {
        return String.format(SKIP_FORM_MODEL_PROP_FORMAT, getBuildTimeSpecPropertyPrefix(openApiFilePath));
    }

    public static String getAdditionalModelTypeAnnotationsPropertyName(final Path openApiFilePath) {
        return String.format(ADDITIONAL_MODEL_TYPE_ANNOTATIONS_PROP_FORMAT, getBuildTimeSpecPropertyPrefix(openApiFilePath));
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
