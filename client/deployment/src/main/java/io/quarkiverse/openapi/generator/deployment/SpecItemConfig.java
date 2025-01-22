package io.quarkiverse.openapi.generator.deployment;

import java.util.Optional;

import io.smallrye.config.WithName;

/*
 * Model for the configuration of this extension.
 * It's used for documentation purposes only.
 * The configuration is consumed in the codegen phase, before build time.
 * Not meant to be used outside this scope.
 * Config items can be applied only on spec
 */
public interface SpecItemConfig extends CommonItemConfig {

    /**
     * Base package for where the generated code for the given OpenAPI specification will be added.
     */
    @WithName("base-package")
    Optional<String> basePackage();

    /**
     * Suffix name for generated api classes
     */
    @WithName("api-name-suffix")
    Optional<String> apiNameSuffix();

    /**
     * Suffix name for generated model classes
     */
    @WithName("model-name-suffix")
    Optional<String> modelNameSuffix();

    /**
     * Prefix name for generated model classes
     */
    @WithName("model-name-prefix")
    Optional<String> modelNamePrefix();

    /**
     * Remove operation id prefix
     */

    @WithName("remove-operation-id-prefix")
    Optional<Boolean> removeOperationIdPrefix();

    /**
     * Remove operation id prefix
     */
    @WithName("remove-operation-id-prefix-delimiter")
    Optional<String> removeOperationIdPrefixDelimiter();

    /**
     * Remove operation id prefix
     */
    @WithName("remove-operation-id-prefix-count")
    Optional<Integer> removeOperationIdPrefixCount();
}
