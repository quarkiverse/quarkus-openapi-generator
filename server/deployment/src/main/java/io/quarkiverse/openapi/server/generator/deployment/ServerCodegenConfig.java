package io.quarkiverse.openapi.server.generator.deployment;

import java.util.Optional;

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
     * The base package to be used to generated sources.
     */
    @WithDefault(DEFAULT_PACKAGE)
    Optional<String> basePackage();

    /**
     * Whether it must generate resources and beans using bean validation (JSR-303).
     */
    @WithDefault("false")
    boolean useBeanValidation();
}
