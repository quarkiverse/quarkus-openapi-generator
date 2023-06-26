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
 */
@ConfigGroup
public class SpecItemConfig {

    /**
     * Base package for where the generated code for the given OpenAPI specification will be added.
     */
    @ConfigItem(name = "base-package")
    public Optional<String> basePackage;

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
     * Provider classes that should be registered on the generated rest client
     */
    @ConfigItem(name = "custom-register-providers")
    public Optional<String> customRegisterProviders;

    /**
     * Defines if the methods should return {@link javax.ws.rs.core.Response} or a model. Default is <code>false</code>.
     */
    @ConfigItem(name = "return-response")
    public Optional<Boolean> returnResponse;

    /**
     * ClientHeaderFactory class that should be used in @RegisterClientHeaders.
     * Full class name is expected
     * By default generated AuthenticationPropagationHeadersFactory class is set.
     * Can also be set to 'none' to use default microprofile implementation (DefaultClientHeadersFactoryImpl)
     */
    @ConfigItem(name = "client-headers-factory")
    public Optional<String> clientHeadersFactory;

    /**
     * Defines the normalizer options.
     */
    @ConfigItem(name = "open-api-normalizer")
    public Map<String, String> normalizer;
}
