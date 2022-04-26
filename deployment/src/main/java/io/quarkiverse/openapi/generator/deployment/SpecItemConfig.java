package io.quarkiverse.openapi.generator.deployment;

// Configuration class for documentation purposes
import io.quarkus.runtime.annotations.ConfigItem;

public class SpecItemConfig {

    /**
     * Base package for where the generated code for the given OpenAPI specification will be added.
     */
    @ConfigItem
    String basePackage;

}
