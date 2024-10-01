package io.quarkiverse.openapi.generator.deployment;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.quarkiverse.openapi.generator.deployment.codegen.OpenApiGeneratorOutputPaths;
import io.smallrye.config.common.utils.StringUtil;

public class CodegenConfigMethods {
    static final String CODEGEN_TIME_CONFIG_PREFIX = "openapi-generator.codegen";

    public static final String API_PKG_SUFFIX = ".api";
    public static final String MODEL_PKG_SUFFIX = ".model";

    public static final String ADDITIONAL_ENUM_TYPE_UNEXPECTED_MEMBER_NAME_DEFAULT = "UNEXPECTED";
    public static final String ADDITIONAL_ENUM_TYPE_UNEXPECTED_MEMBER_STRING_VALUE_DEFAULT = "unexpected";
    // package visibility for unit tests
    static final String BUILD_TIME_GLOBAL_PREFIX_FORMAT = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".%s";
    static final String BUILD_TIME_SPEC_PREFIX_FORMAT = "quarkus." + CODEGEN_TIME_CONFIG_PREFIX + ".spec.%s";

    public static final List<String> SUPPORTED_CONFIGURATIONS = Arrays.stream(CodegenConfigName.values()).map(cn -> cn.name)
            .collect(Collectors.toList());

    /**
     * Return spec config name openapi-generator.codegen.spec.%s.config-name
     */
    public static String getSpecConfigName(CodegenConfigName configName, final Path openApiFilePath) {
        return String.format("%s.%s", getBuildTimeSpecPropertyPrefix(openApiFilePath), configName.name);
    }

    /**
     * Return spec con fig name by config-key (<b>openapi-generator.codegen.spec.%s.config-key</b>) property.
     * For example, given a configuration <code>quarkus.openapi.generator.codegen.spec.spec_yaml.config-key=petstore</code>, the
     * returned value is
     * <code>openapi.generator.codegen.spec.petstore.mutiny</code>.
     */
    public static String getSpecConfigNameByConfigKey(final String configKey, final CodegenConfigName configName) {
        String buildTimeSpecPropertyPrefix = String.format(BUILD_TIME_SPEC_PREFIX_FORMAT, configKey);
        return String.format("%s.%s", buildTimeSpecPropertyPrefix, configName.name);
    }

    /**
     * Gets the config prefix for a given OpenAPI file in the path.
     * For example, given a path like /home/luke/projects/petstore.json, the returned value is
     * `quarkus.openapi-generator."petstore_json"`.
     * Every the periods (.) in the file name will be replaced by underscore (_).
     */

    public static String resolveApiPackage(final String basePackage) {
        return String.format("%s%s", basePackage, API_PKG_SUFFIX);
    }

    public static String resolveModelPackage(final String basePackage) {
        return String.format("%s%s", basePackage, MODEL_PKG_SUFFIX);
    }

    /**
     * Return global config name, openapi-generator.codegen.config-name
     */
    public static String getGlobalConfigName(CodegenConfigName configName) {
        return String.format(BUILD_TIME_GLOBAL_PREFIX_FORMAT, configName.name);
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
