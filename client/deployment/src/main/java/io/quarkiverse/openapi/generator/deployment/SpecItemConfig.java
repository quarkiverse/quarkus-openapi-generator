package io.quarkiverse.openapi.generator.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/*
 * Model for the configuration of this extension.
 * It's used for documentation purposes only.
 * The configuration is consumed in the codegen phase, before build time.
 * Not meant to be used outside this scope.
 * Config items can be applied only on spec
 */
@ConfigGroup
public class SpecItemConfig extends CommonItemConfig {

    /**
     * Base package for where the generated code for the given OpenAPI specification will be added.
     */
    @ConfigItem(name = "base-package")
    public Optional<String> basePackage;

    /**
     * Suffix name for generated api classes
     */
    @ConfigItem(name = "api-name-suffix")
    public Optional<String> apiNameSuffix;

    /**
     * Suffix name for generated model classes
     */
    @ConfigItem(name = "model-name-suffix")
    public Optional<String> modelNameSuffix;

    /**
     * Prefix name for generated model classes
     */
    @ConfigItem(name = "model-name-prefix")
    public Optional<String> modelNamePrefix;

    /**
     * Remove operation id prefix
     */
    @ConfigItem(name = "remove-operation-id-prefix")
    public Optional<Boolean> removeOperationIdPrefix;

    /**
     * Remove operation id prefix
     */
    @ConfigItem(name = "remove-operation-id-prefix-delimiter")
    public Optional<String> removeOperationIdPrefixDelimiter;

    /**
     * Remove operation id prefix
     */
    @ConfigItem(name = "remove-operation-id-prefix-count")
    public Optional<Integer> removeOperationIdPrefixCount;
}
