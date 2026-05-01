package io.quarkiverse.openapi.server.generator.deployment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

public interface ServerCodegenConfig {

    String DEFAULT_APICURIO_PACKAGE = "io.apicurio.api";
    String DEFAULT_PACKAGE = "org.acme";
    String DEFAULT_DIR = "openapi";
    String APICURIO = "apicurio";
    String OPENAPITOOLS = "openapitools";

    /**
     * The OpenAPI specification filename.
     *
     * @deprecated Use {@code quarkus.openapi.generator.server.<tool>.spec} instead.
     */
    @Deprecated(forRemoval = true)
    Optional<String> spec();

    /**
     * The input base dir where the OpenAPI specification is.
     * <p>
     *
     * @deprecated Use {@code quarkus.openapi.generator.server.<tool>.input-base-dir} instead.
     */
    @Deprecated(forRemoval = true)
    Optional<String> inputBaseDir();

    /**
     * Whether it must generate with reactive code.
     * <p>
     *
     * @deprecated Use {@code quarkus.openapi.generator.server.<tool>.use-reactive} instead.
     */
    @Deprecated(forRemoval = true)
    Optional<Boolean> reactive();

    /**
     * Whether it must generate builders for properties.
     * <p>
     *
     * @deprecated {@code quarkus.openapi.generator.server.<tool>.use-builders} instead.
     */
    @Deprecated(forRemoval = true)
    Optional<Boolean> builders();

    /**
     * The base package to be used to generated sources.
     * <p>
     *
     * @deprecated Use {@code quarkus.openapi.generator.server.<tool>.base-package} instead.
     */
    @Deprecated(forRemoval = true)
    Optional<String> basePackage();

    /**
     * Whether it must generate resources and beans using bean validation (JSR-303).
     *
     * @deprecated Use {@code quarkus.openapi.generator.server.<tool>.use-bean-validation} instead.
     */
    @Deprecated(forRemoval = true)
    Optional<Boolean> useBeanValidation();

    ServerConfig server();

    @ConfigGroup
    interface ServerConfig {

        /**
         * The generator to be used for generating the server code.
         * <p>
         * Possible values are: <code>apicurio</code> or <code>openapitools</code>.
         * <p>
         * By default, is <code>apicurio</code>.
         */
        @WithDefault(APICURIO)
        Optional<String> use();

        /**
         * The OpenAPI specification filename.
         */
        Optional<String> spec();

        /**
         * The input base dir where the OpenAPI specification is.
         */
        @WithDefault("src/main/resources/openapi")
        Optional<String> inputBaseDir();

        /**
         * Whether it must generate with reactive code.
         */
        @WithDefault("false")
        Optional<Boolean> useReactive();

        /**
         * Whether it must generate builders for properties.
         */
        @WithDefault("false")
        Optional<Boolean> useBuilder();

        /**
         * The base package to be used to generated sources.
         */
        @WithDefault(DEFAULT_PACKAGE)
        Optional<String> basePackage();

        /**
         * Whether it must generate resources and beans using bean validation (JSR-303).
         */
        @WithDefault("false")
        Optional<Boolean> useBeanValidation();

        /**
         * Whether the generated server methods should use {@code org.jboss.resteasy.reactive.RestResponse<T>}
         * as the return type for single-response endpoints.
         * When enabled, methods return {@code RestResponse<Model>} (or {@code Uni<RestResponse<Model>>} in
         * reactive mode), allowing implementors to control the HTTP response status code (e.g. 201 Created).
         * Streaming endpoints are out of scope for this flag.
         * This property only applies when using the {@code openapitools} code generator
         * (i.e. when {@code quarkus.openapi.generator.server.use} is set to {@code openapitools}).
         * By default this is {@code false}, preserving backward-compatible return types.
         */
        @WithDefault("false")
        Optional<Boolean> useRestResponse();

        /**
         * Whether to skip generation when the persisted fingerprint of the OpenAPI specification
         * and relevant generation configuration matches the previous run.
         */
        @WithDefault("false")
        Optional<Boolean> skipIfUnchanged();

        /**
         * List of OpenAPI file names to include for code generation.
         * When set, only the listed files are processed by the auto-discovery mechanism.
         */
        Optional<List<String>> include();

        /**
         * List of OpenAPI file names to exclude from code generation.
         * When set, the listed files are skipped by the auto-discovery mechanism.
         * This is useful for excluding shared reference files (e.g., {@code common-spec.yaml})
         * that are not standalone specifications.
         */
        Optional<List<String>> exclude();

        /**
         * The map of operations to be configured, where the key is the operationId defined in the OpenAPI specification and the
         * value is the configuration for that operation.
         * <p>
         * Only applied for {@code apicurio} generator.
         */
        Map<String, OperationConfig> operationIds();

    }
}
