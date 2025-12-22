package io.quarkiverse.openapi.server.generator.deployment;

import java.util.Optional;

import io.quarkiverse.openapi.server.generator.deployment.codegen.ApicurioConfig;
import io.quarkiverse.openapi.server.generator.deployment.codegen.OpenAPIToolsConfig;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

public interface ServerCodegenConfig {

    String DEFAULT_PACKAGE = "io.apicurio.api";
    String DEFAULT_DIR = "openapi";
    String APICURIO = "apicurio";
    String OPENAPITOOLS = "openapitools";

    /**
     * The OpenAPI specification filename.
     */
    Optional<String> spec();

    /**
     * The input base dir where the OpenAPI specification is.
     * <p>
     *
     * @deprecated Use {@code quarkus.openapi.generator.apicurio.input-base-dir} instead.
     */
    @WithDefault("src/main/resources/openapi")
    @Deprecated
    Optional<String> inputBaseDir();

    /**
     * Whether it must generate with reactive code.
     * <p>
     *
     * @deprecated Use {@code quarkus.openapi.generator.apicurio.reactive} instead.
     */
    @Deprecated
    @WithDefault("false")
    boolean reactive();

    /**
     * Whether it must generate builders for properties.
     * <p>
     *
     * @deprecated {@code quarkus.openapi.generator.apicurio.generate-builders} instead.
     */
    @Deprecated
    @WithDefault("false")
    boolean builders();

    /**
     * The base package to be used to generated sources.
     * <p>
     *
     * @deprecated Use {@code quarkus.openapi.generator.apicurio.base-package} instead.
     */
    @Deprecated
    @WithDefault(DEFAULT_PACKAGE)
    Optional<String> basePackage();

    /**
     * Whether it must generate resources and beans using bean validation (JSR-303).
     *
     * @deprecated Use {@code quarkus.openapi.generator.apicurio.use-bean-validation} instead.
     */
    @Deprecated
    @WithDefault("false")
    boolean useBeanValidation();

    Server server();

    @ConfigGroup
    interface Server {

        /**
         * The generator to be used for generating the server code.
         * <p>
         * Possible values are: <code>apicurio</code> or <code>openapitools</code>.
         * <p>
         * By default, is <code>apicurio</code>.
         */
        @WithDefault(APICURIO)
        String use();

        ApicurioConfig apicurio();

        OpenAPIToolsConfig openapitools();
    }
}
