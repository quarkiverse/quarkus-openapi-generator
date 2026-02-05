package io.quarkiverse.openapi.server.generator.deployment;

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
     * @deprecated {@code quarkus.openapi.generator.server.<tool>.use-builder} instead.
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

    }
}
