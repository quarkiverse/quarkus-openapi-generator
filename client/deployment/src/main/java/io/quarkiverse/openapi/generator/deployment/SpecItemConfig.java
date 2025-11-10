package io.quarkiverse.openapi.generator.deployment;

import java.util.List;
import java.util.Optional;

import io.smallrye.config.WithDefault;
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
     * Custom config key to use in place of the openapi spec file
     */
    @WithName("config-key")
    Optional<String> configKey();

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

    /**
     * Set serializable model
     */
    @WithName("serializable-model")
    Optional<Boolean> serializableModel();

    /**
     * Whether to enable Dynamic URLs on APIs methods.
     * By enabling this property every method on `RestClients` will be annotated with `io.quarkus.rest.client.reactive.Url`.
     *
     * @see <a href="https://quarkus.io/version/3.20/guides/rest-client#dynamic-base-urls">Dynamic base URLs</a>
     */
    @WithName("use-dynamic-url")
    @WithDefault("false")
    Optional<Boolean> useDynamicUrl();

    /**
     * List of OpenAPI spec files in GAV to be generated
     */
    @WithName("gav-spec-files")
    @WithDefault("openapi.yaml")
    Optional<List<String>> gavSpecFiles();

}
