package io.quarkiverse.openapi.generator;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class SpecConfig {

    /**
     * Detailed configuration for Authentication schemas in runtime.
     */
    @ConfigItem
    public AuthConfig auth;

}
