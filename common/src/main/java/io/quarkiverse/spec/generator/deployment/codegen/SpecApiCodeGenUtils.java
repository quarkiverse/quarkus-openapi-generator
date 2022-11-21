package io.quarkiverse.spec.generator.deployment.codegen;

import java.nio.file.Path;

import io.smallrye.config.common.utils.StringUtil;

public final class SpecApiCodeGenUtils {

    private SpecApiCodeGenUtils() {
    }

    private static final String BUILD_TIME_SPEC_PREFIX_FORMAT = "quarkus.%s.spec.%s";
    private static final String BASE_PACKAGE_PROP_FORMAT = "%s.base-package";

    public static String getSanitizedFileName(final Path openApiFilePath) {
        return StringUtil
                .replaceNonAlphanumericByUnderscores(
                        SpecApiGeneratorOutputPaths.getRelativePath(openApiFilePath).toString());
    }

    public static String getBuildTimeSpecPropertyPrefix(final Path openApiFilePath, String configPrefix) {
        return String.format(BUILD_TIME_SPEC_PREFIX_FORMAT, configPrefix, getSanitizedFileName(openApiFilePath));
    }

    public static String getBasePackagePropertyName(final Path openApiFilePath, String configPrefix) {
        return String.format(BASE_PACKAGE_PROP_FORMAT, getBuildTimeSpecPropertyPrefix(openApiFilePath, configPrefix));
    }

}
