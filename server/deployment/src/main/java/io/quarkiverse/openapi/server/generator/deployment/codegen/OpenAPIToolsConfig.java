package io.quarkiverse.openapi.server.generator.deployment.codegen;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface OpenAPIToolsConfig {

    String DEFAULT_BASE_PACKAGE = "org.acme";

    /**
     * The base package to be used to generated sources.
     */
    @WithDefault(DEFAULT_BASE_PACKAGE)
    String basePackage();

    /**
     * Whether OpenAPITools must generate resources and beans using bean validation (JSR-303).
     */
    @WithDefault("true")
    String useBeanValidation();

    /**
     * The OpenAPI specification filename.
     */
    String spec();

    /**
     * The input base dir where the OpenAPI specification is.
     */
    @WithDefault("src/main/resources/openapi")
    Optional<String> inputBaseDir();
}
