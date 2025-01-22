package io.quarkiverse.openapi.generator.deployment;

import java.util.Map;
import java.util.Optional;

import io.smallrye.config.WithName;

/*
 * Model for the configuration of this extension.
 * It's used for documentation purposes only.
 * The configuration is consumed in the codegen phase, before build time.
 * Not meant to be used outside this scope.
 * Config items can be applied on spec and globally as well
 */
public interface CommonItemConfig {

    /**
     * Whether to skip the generation of models for form parameters
     */
    @WithName("skip-form-model")
    Optional<Boolean> skipFormModel();

    /**
     * Type Mapping is an OpenAPI Generator configuration specifying which Java types (the values) should be used for a
     * given OAS datatype (the keys of this map)
     */
    @WithName("type-mappings")
    Map<String, String> typeMappings();

    /**
     * Import Mapping is an OpenAPI Generator configuration specifying which Java types (the values) should be
     * imported when a given OAS datatype (the keys of this map) is used
     */
    @WithName("import-mappings")
    Map<String, String> importMappings();

    /**
     * Schema Mapping is an OpenAPI Generator configuration specifying which Java types (the values) should be
     * imported when a given schema type (the keys of this map) is used
     */
    @WithName("schema-mappings")
    Map<String, String> schemaMappings();

    /**
     * The specified annotations will be added to the generated model files
     */
    @WithName("additional-model-type-annotations")
    Optional<String> additionalModelTypeAnnotations();

    /**
     * Defines if the enums should have an `UNEXPECTED` member to convey values that cannot be parsed. Default is
     * {@code false}.
     */
    @WithName("additional-enum-type-unexpected-member")
    Optional<Boolean> additionalEnumTypeUnexpectedMemberAnnotations();

    /**
     * The specified annotations will be added to the generated api files
     */
    @WithName("additional-api-type-annotations")
    Optional<String> additionalApiTypeAnnotations();

    /**
     * Add custom/additional HTTP Headers or other args to every request
     */
    @WithName("additional-request-args")
    Optional<String> additionalRequestArgs();

    /**
     * Defines if the methods should return {@link jakarta.ws.rs.core.Response} or a model. Default is {@code false}.
     */
    @WithName("return-response")
    Optional<Boolean> returnResponse();

    /**
     * Defines if security support classes should be generated
     */
    @WithName("enable-security-generation")
    Optional<String> enableSecurityGeneration();

    /**
     * Defines the normalizer options.
     */
    @WithName("open-api-normalizer")
    Map<String, String> normalizer();

    /**
     * Enable SmallRye Mutiny support. If you set this to {@code true}, all return types will be wrapped in
     * {@link io.smallrye.mutiny.Uni}.
     */
    @WithName("mutiny")
    Optional<Boolean> supportMutiny();

    /**
     * Defines with SmallRye Mutiny enabled if methods should return {@link jakarta.ws.rs.core.Response} or a model. Default is
     * {@code false}.
     */
    @WithName("mutiny.return-response")
    Optional<Boolean> mutinyReturnResponse();

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
    @WithName("mutiny.operation-ids")
    Map<String, String> mutinyMultiOperationIds();

    /**
     * Defines, whether the `PartFilename` ({@link org.jboss.resteasy.reactive.PartFilename} or
     * {@link org.jboss.resteasy.annotations.providers.multipart.PartFilename}) annotation should be generated for
     * MultipartForm POJOs. By setting to {@code false}, the annotation will not be generated.
     */
    @WithName("generate-part-filename")
    Optional<Boolean> generatePartFilename();

    /**
     * Defines the filename for a part in case the `PartFilename` annotation
     * ({@link org.jboss.resteasy.reactive.PartFilename} or
     * {@link org.jboss.resteasy.annotations.providers.multipart.PartFilename}) is generated.
     * In case no value is set, the default one is `&lt;fieldName&gt;File` or `file`, depending on the
     * {@link CommonItemConfig#useFieldNameInPartFilename} configuration.
     */
    @WithName("part-filename-value")
    Optional<String> partFilenameValue();

    /**
     * Defines, whether the filename should also include the property name in case the `PartFilename` annotation
     * ({@link org.jboss.resteasy.reactive.PartFilename} or
     * {@link org.jboss.resteasy.annotations.providers.multipart.PartFilename}) is generated.
     */
    @WithName("use-field-name-in-part-filename")
    Optional<Boolean> useFieldNameInPartFilename();

    /**
     * Enable bean validation. If you set this to {@code true}, validation annotations are added to generated sources E.g.
     * {@code @Size}.
     */
    @WithName("use-bean-validation")
    Optional<Boolean> useBeanValidation();

    /**
     * Enable the generation of APIs. If you set this to {@code false}, APIs will not be generated.
     */
    @WithName("generate-apis")
    Optional<Boolean> generateApis();

    /**
     * Enable the generation of models. If you set this to {@code false}, models will not be generated.
     */
    @WithName("generate-models")
    Optional<Boolean> generateModels();

    /**
     * Enable the generation of equals and hashcode in models. If you set this to {@code false}, the models
     * will not have equals and hashcode.
     */
    @WithName("equals-hashcode")
    Optional<Boolean> equalsHashcode();
}
