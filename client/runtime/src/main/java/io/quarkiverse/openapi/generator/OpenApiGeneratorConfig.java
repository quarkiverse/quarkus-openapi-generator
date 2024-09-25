package io.quarkiverse.openapi.generator;

import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

/**
 * This class represents the runtime configurations for the openapi-generator extension.
 */
@ConfigMapping(prefix = OpenApiGeneratorConfigMethods.RUNTIME_TIME_CONFIG_PREFIX)
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface OpenApiGeneratorConfig {
    /**
     * Configurations of the individual OpenApi spec definitions, i.e. the provided files.
     * <p>
     * The key must be any of the sanitized names of the OpenApi definition files.
     * For example, a file named petstore.json is sanitized into the name petstore_json, and thus the specific
     * configurations this file must start with the prefix quarkus.openapi-generator.petstore_json
     */
    @WithName(ConfigItem.PARENT)
    Map<String, Optional<SpecItemConfig>> itemConfigs();
}
