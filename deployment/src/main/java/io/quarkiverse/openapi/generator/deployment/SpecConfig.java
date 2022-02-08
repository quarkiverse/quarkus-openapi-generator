package io.quarkiverse.openapi.generator.deployment;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class SpecConfig {
    /**
     * Defines the base package name for the generated API classes
     */
    @ConfigItem(defaultValue = "io.quarkiverse.openapi.generator.api")
    public String apiPackage;

    /**
     * Defines the base package name for the generated Model classes
     */
    @ConfigItem(defaultValue = "io.quarkiverse.openapi.generator.model")
    public String modelPackage;
}
