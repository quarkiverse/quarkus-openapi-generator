package io.quarkiverse.openapi.generator.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/*
 * Model for the configuration of this extension.
 * It's used for documentation purposes only.
 * The configuration is consumed in the codegen phase, before build time.
 * Not meant to be used outside this scope.
 */
@ConfigGroup
public class SpecItemBuildTimeConfig {

    /**
     * Base package for where the generated code for the given OpenAPI specification will be added.
     */
    @ConfigItem(name = "base-package")
    public String basePackage;

    /**
     * Whether to skip the generation of models for form parameters
     */
    @ConfigItem(name = "skip-form-model")
    public Optional<Boolean> skipFormModel;

}
