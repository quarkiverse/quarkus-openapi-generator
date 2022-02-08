package io.quarkiverse.openapi.generator.deployment;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME, prefix = "quarkus.openapi-generator")
public class CodeGenConfiguration {
    /**
     * Defines the base package name for the generated API classes
     */
    @ConfigItem(defaultValue = "io.quarkiverse.openapi.generator.api")
    String apiPackage;

    /**
     * Defines the base package name for the generated Model classes
     */
    @ConfigItem(defaultValue = "io.quarkiverse.openapi.generator.model")
    String modelPackage;

    /**
     * Increases the internal generator log output verbosity
     */
    @ConfigItem(defaultValue = "false")
    String verbose;

    public String getApiPackage() {
        return apiPackage;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    public String getVerbose() {
        return verbose;
    }
}
