package io.quarkiverse.openapi.generator;

import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;
import io.smallrye.config.common.utils.StringUtil;

/**
 * This class represents the runtime configurations for the openapi-generator extension.
 */
@ConfigMapping(prefix = "quarkus." + OpenApiGeneratorConfig.RUNTIME_TIME_CONFIG_PREFIX)
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface OpenApiGeneratorConfig {

    String RUNTIME_TIME_CONFIG_PREFIX = "openapi-generator";

    /**
     * Configurations of the individual OpenApi spec definitions, i.e. the provided files.
     * <p>
     * The key must be any of the sanitized names of the OpenApi definition files.
     * For example, a file named petstore.json is sanitized into the name petstore_json, and thus the specific
     * configurations this file must start with the prefix quarkus.openapi-generator.petstore_json
     */
    @WithParentName
    Map<String, SpecItemConfig> itemConfigs();

    default Optional<SpecItemConfig> getItemConfig(String specItem) {
        return Optional.ofNullable(itemConfigs().get(specItem));
    }

    static String getSanitizedSecuritySchemeName(final String securitySchemeName) {
        return StringUtil.replaceNonAlphanumericByUnderscores(securitySchemeName);
    }
}
