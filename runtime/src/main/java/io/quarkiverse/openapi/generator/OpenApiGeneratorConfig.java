package io.quarkiverse.openapi.generator;

import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.common.utils.StringUtil;

/**
 * This class represents the runtime configurations for the openapi-generator extension.
 */
@ConfigRoot(name = OpenApiGeneratorConfig.RUNTIME_TIME_CONFIG_PREFIX, phase = ConfigPhase.RUN_TIME)
public class OpenApiGeneratorConfig {

    public static final String RUNTIME_TIME_CONFIG_PREFIX = "openapi-generator";

    /**
     * Configurations of the individual OpenApi spec definitions, i.e. the provided files.
     * <p>
     * The key must be any of the sanitized names of the OpenApi definition files.
     * For example, a file named petstore.json is sanitized into the name petstore_json, and thus the specific
     * configurations this file must start with the prefix quarkus.openapi-generator.petstore_json
     */
    @ConfigItem(name = ConfigItem.PARENT)
    public Map<String, SpecItemConfig> itemConfigs;

    public Optional<SpecItemConfig> getItemConfig(String specItem) {
        return Optional.ofNullable(itemConfigs.get(specItem));
    }

    @Override
    public String toString() {
        return "OpenApiGeneratorConfig{" +
                "itemConfigs=" + itemConfigs +
                '}';
    }

    public static String getSanitizedSecuritySchemeName(final String securitySchemeName) {
        return StringUtil.replaceNonAlphanumericByUnderscores(securitySchemeName);
    }
}
