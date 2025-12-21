package io.quarkiverse.openapi.server.generator.deployment.codegen;

import static io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig.DEFAULT_PACKAGE;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface ApicurioConfig {
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
     * Whether Apicurio must generate with reactive code.
     */
    @WithDefault("false")
    boolean reactive();

    /**
     * Whether Apicurio must generate builders for properties.
     */
    @WithDefault("false")
    boolean builders();

    /**
     * The base package to be used to generated sources.
     */
    @WithDefault(DEFAULT_PACKAGE)
    Optional<String> basePackage();

    /**
     * Whether Apicurio must generate resources and beans using bean validation (JSR-303).
     */
    boolean useBeanValidation();
}
