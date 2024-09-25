package io.quarkiverse.openapi.generator.deployment;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

// This configuration is read in codegen phase (before build time), the annotation is for document purposes and avoiding quarkus warns
@ConfigMapping(prefix = "openapi-generator.codegen")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface CodegenConfig {
    /**
     * Common item test.
     */
    @WithName("common-item-config")
    CommonItemConfig commonItemConfig();

    /**
     * OpenAPI Spec details for codegen configuration.
     */
    @WithName("spec")
    Map<String, SpecItemConfig> specItem();
    //
}
