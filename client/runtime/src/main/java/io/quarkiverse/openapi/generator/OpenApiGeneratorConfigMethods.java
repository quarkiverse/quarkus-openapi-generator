package io.quarkiverse.openapi.generator;

import io.smallrye.config.common.utils.StringUtil;

public class OpenApiGeneratorConfigMethods {
    public static final String RUNTIME_TIME_CONFIG_PREFIX = "openapi-generator";

    public static String getSanitizedSecuritySchemeName(final String securitySchemeName) {
        return StringUtil.replaceNonAlphanumericByUnderscores(securitySchemeName);
    }
}
