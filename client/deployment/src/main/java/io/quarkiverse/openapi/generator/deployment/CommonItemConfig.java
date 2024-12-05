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
     * Schema Mapping is an OpenAPI Generator configuration specifying which Java types (the values) should be
     * imported when a given schema type (the keys of this map) is used
     */
    @ConfigItem(name = "schema-mappings")
    public Map<String, String> schemaMappings;

    /**
     * The specified annotations will be added to the generated model files
     */
    @ConfigItem(name = "additional-model-type-annotations")
    public Optional<String> additionalModelTypeAnnotations;

    /**
     * Defines if the enums should have an `UNEXPECTED` member to convey values that cannot be parsed. Default is
     * {@code false}.
     */
    @ConfigItem(name = "additional-enum-type-unexpected-member")
    public Optional<Boolean> additionalEnumTypeUnexpectedMemberAnnotations;

    /**
     * The specified annotations will be added to the generated api files
     */
    @ConfigItem(name = "additional-api-type-annotations")
    public Optional<String> additionalApiTypeAnnotations;

    /**
     * Add custom/additional HTTP Headers or other args to every request
     */
    @ConfigItem(name = "additional-request-args")
    public Optional<String> additionalRequestArgs;

    /**
     * Defines if the methods should return {@link jakarta.ws.rs.core.Response} or a model. Default is {@code false}.
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
     * Enable SmallRye Mutiny support. If you set this to {@code true}, all return types will be wrapped in
     * {@link io.smallrye.mutiny.Uni}.
     */
    @ConfigItem(name = "mutiny")
    public Optional<Boolean> supportMutiny;

    /**
     * Defines with SmallRye Mutiny enabled if methods should return {@link jakarta.ws.rs.core.Response} or a model. Default is
     * {@code false}.
     */
    @ConfigItem(name = "mutiny.return-response")
    public Optional<Boolean> mutinyReturnResponse;

    /**
     * Handles the return type for each operation, depending on the configuration.
     * The following cases are supported:
     * <p>
     * 1. If {@code mutiny} is enabled and the operation ID is specified to return {@code Multi}:
     * - The return type will be wrapped in {@link io.smallrye.mutiny.Multi}.
     * - If {@code mutiny.return-response} is enabled, the return type will be
     * {@link io.smallrye.mutiny.Multi<jakarta.ws.rs.core.Response>}.
     * - If the operation has a void return type, it will return {@link io.smallrye.mutiny.Multi<jakarta.ws.rs.core.Response>}.
     * - Otherwise, it will return {@link io.smallrye.mutiny.Multi<returnType>}.
     * <p>
     * 2. If {@code mutiny} is enabled and the operation ID is specified to return {@code Uni}:
     * - The return type will be wrapped in {@link io.smallrye.mutiny.Uni}.
     * - If {@code mutiny.return-response} is enabled, the return type will be
     * {@link io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>}.
     * - If the operation has a void return type, it will return {@link io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>}.
     * - Otherwise, it will return {@link io.smallrye.mutiny.Uni<returnType>}.
     * <p>
     * 3. If {@code mutiny} is enabled but no specific operation ID is configured for {@code Multi} or {@code Uni}:
     * - The return type defaults to {@code Uni}.
     * - If {@code mutiny.return-response} is enabled, the return type will be
     * {@link io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>}.
     * - If the operation has a void return type, it will return {@link io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>}.
     * - Otherwise, it will return {@link io.smallrye.mutiny.Uni<returnType>}`.
     */
    @ConfigItem(name = "mutiny.operation-ids")
    public Optional<Map<String, String>> mutinyMultiOperationIds;

    /**
     * Defines, whether the `PartFilename` ({@link org.jboss.resteasy.reactive.PartFilename} or
     * {@link org.jboss.resteasy.annotations.providers.multipart.PartFilename}) annotation should be generated for
     * MultipartForm POJOs. By setting to {@code false}, the annotation will not be generated.
     */
    @ConfigItem(name = "generate-part-filename")
    public Optional<Boolean> generatePartFilename;

    /**
     * Defines the filename for a part in case the `PartFilename` annotation
     * ({@link org.jboss.resteasy.reactive.PartFilename} or
     * {@link org.jboss.resteasy.annotations.providers.multipart.PartFilename}) is generated.
     * In case no value is set, the default one is `&lt;fieldName&gt;File` or `file`, depending on the
     * {@link CommonItemConfig#useFieldNameInPartFilename} configuration.
     */
    @ConfigItem(name = "part-filename-value")
    public Optional<String> partFilenameValue;

    /**
     * Defines, whether the filename should also include the property name in case the `PartFilename` annotation
     * ({@link org.jboss.resteasy.reactive.PartFilename} or
     * {@link org.jboss.resteasy.annotations.providers.multipart.PartFilename}) is generated.
     */
    @ConfigItem(name = "use-field-name-in-part-filename")
    public Optional<Boolean> useFieldNameInPartFilename;

    /**
     * Enable bean validation. If you set this to {@code true}, validation annotations are added to generated sources E.g.
     * {@code @Size}.
     */
    @ConfigItem(name = "use-bean-validation")
    public Optional<Boolean> useBeanValidation;

    /**
     * Enable the generation of APIs. If you set this to {@code false}, APIs will not be generated.
     */
    @ConfigItem(name = "generate-apis")
    public Optional<Boolean> generateApis;

    /**
     * Enable the generation of models. If you set this to {@code false}, models will not be generated.
     */
    @ConfigItem(name = "generate-models")
    public Optional<Boolean> generateModels;
}
