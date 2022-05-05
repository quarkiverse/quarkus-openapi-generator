package io.quarkiverse.openapi.generator.deployment;

import java.nio.file.Path;
import java.util.Map;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.common.utils.StringUtil;

// This configuration is read in codegen phase (before build time), the annotation is for document purposes and avoiding quarkus warns
@ConfigRoot(name = SpecConfig.BUILD_TIME_CONFIG_PREFIX, phase = ConfigPhase.BUILD_TIME)
public class SpecConfig {

    static final String BUILD_TIME_CONFIG_PREFIX = "openapi-generator.codegen";
    public static final String API_PKG_SUFFIX = ".api";
    public static final String MODEL_PKG_SUFFIX = ".model";
    // package visibility for unit tests
    static final String BUILD_TIME_SPEC_PREFIX_FORMAT = "quarkus." + BUILD_TIME_CONFIG_PREFIX + ".spec.%s";
    private static final String BASE_PACKAGE_PROP_FORMAT = "%s.base-package";
    private static final String SKIP_FORM_MODEL_PROP_FORMAT = "%s.skip-form-model";

    /**
     * OpenAPI Spec details for codegen configuration.
     */
    @ConfigItem(name = "spec")
    public Map<String, SpecItemConfig> specItem;

    public static String resolveApiPackage(final String basePackage) {
        return String.format("%s%s", basePackage, API_PKG_SUFFIX);
    }

    public static String resolveModelPackage(final String basePackage) {
        return String.format("%s%s", basePackage, MODEL_PKG_SUFFIX);
    }

    public static String getResolvedBasePackagePropertyName(final Path openApiFilePath) {
        return String.format(BASE_PACKAGE_PROP_FORMAT, getBuildTimeSpecPropertyPrefix(openApiFilePath));
    }

    public static String getSkipFormModelPropertyName(final Path openApiFilePath) {
        return String.format(SKIP_FORM_MODEL_PROP_FORMAT, getBuildTimeSpecPropertyPrefix(openApiFilePath));
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
        final String fileName = openApiFilePath.getFileName().toString();
        return StringUtil.replaceNonAlphanumericByUnderscores(fileName.substring(fileName.lastIndexOf("/") + 1));
    }
}
