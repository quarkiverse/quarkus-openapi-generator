package io.quarkiverse.openapi.server.generator.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

public interface ServerCodegenConfig {

    String DEFAULT_PACKAGE = "io.apicurio.api";
    String DEFAULT_DIR = "openapi";

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
    boolean reactive();

    /**
     * Whether it must generate builders for properties.
     */
    @WithDefault("false")
    boolean builders();

    /**
     * The base package to be used to generated sources.
     */
    @WithDefault(DEFAULT_PACKAGE)
    Optional<String> basePackage();

    /**
     * Whether it must generate resources and beans using bean validation (JSR-303).
     */
    @WithDefault("false")
    boolean useBeanValidation();

    Server server();

    @ConfigGroup
    interface Server {

        /**
         * The generator to be used for generating the server code.
         * <p>
         * Possible values are: <code>҆apicurio</code> or <code>openapitools</code>.
         * <p>
         * By default is <code>apicurio</code>.
         */
        @WithDefault("apicurio")
        String use();

    }
}
