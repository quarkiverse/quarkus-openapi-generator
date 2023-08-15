package io.quarkiverse.openapi.generator.deployment;

import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/*
 * Model for the configuration of this extension.
 * It's used for documentation purposes only.
 * The configuration is consumed in the codegen phase, before build time.
 * Not meant to be used outside this scope.
 * Config items can be applied on spec and globally as well
 */
@ConfigGroup
public class CommonItemConfig {

    /**
     * Whether to skip the generation of models for form parameters
     */
    @ConfigItem(name = "skip-form-model")
    public Optional<Boolean> skipFormModel;

    /**
     * Type Mapping is an OpenAPI Generator configuration specifying which Java types (the values) should be used for a
     * given OAS datatype (the keys of this map)
     */
    @ConfigItem(name = "type-mappings")
    public Map<String, String> typeMappings;

    /**
     * Import Mapping is an OpenAPI Generator configuration specifying which Java types (the values) should be
     * imported when a given OAS datatype (the keys of this map) is used
     */
    @ConfigItem(name = "import-mappings")
    public Map<String, String> importMappings;

    /**
     * The specified annotations will be added to the generated model files
     */
    @ConfigItem(name = "additional-model-type-annotations")
    public Optional<String> additionalModelTypeAnnotations;

    /**
     * The specified annotations will be added to the generated api files
     */
    @ConfigItem(name = "additional-api-type-annotations")
    public Optional<String> additionalApiTypeAnnotations;

    /**
     * Defines if the methods should return {@link jakarta.ws.rs.core.Response} or a model. Default is <code>false</code>.
     */
    @ConfigItem(name = "return-response")
    public Optional<Boolean> returnResponse;

    /**
     * Defines if security support classes should be generated
     */
    @ConfigItem(name = "enable-security-generation")
    public Optional<String> enableSecurityGeneration;

    /**
     * Defines the normalizer options.
     */
    @ConfigItem(name = "open-api-normalizer")
    public Map<String, String> normalizer;

    /**
     * Enable SmallRye Mutiny support. If you set this to `true`, all return types will be wrapped in `io.smallrye.mutiny.Uni`.
     */
    @ConfigItem(name = "mutiny")
    public Optional<Boolean> supportMutiny;
}
