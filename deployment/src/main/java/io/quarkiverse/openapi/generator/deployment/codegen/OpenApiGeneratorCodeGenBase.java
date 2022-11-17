package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.xapi.generator.deployment.codegen.CodegenConfig.*;

import io.quarkiverse.xapi.generator.deployment.codegen.XApiGeneratorCodeGenBase;

/**
 * Code generation for OpenApi Client. Generates Java classes from OpenApi spec files located in src/main/openapi or
 * src/test/openapi
 * <p>
 * Wraps the <a href="https://openapi-generator.tech/docs/generators/java">OpenAPI Generator Client for Java</a>
 */
public abstract class OpenApiGeneratorCodeGenBase extends XApiGeneratorCodeGenBase {

    public OpenApiGeneratorCodeGenBase() {
        super(new OpenApiGeneratorCodeGenerator());
    }

    static final String DEFAULT_PACKAGE = "org.openapi.quarkus";

    static final String INPUT_DIR = "openapi";

    static final String PROVIDER_PREFIX = "open-api";

    @Override
    public String providerPrefix() {
        return PROVIDER_PREFIX;
    }

    @Override
    public String inputDirectory() {
        return INPUT_DIR;
    }

    @Override
    protected String getDefaultPackage() {
        return DEFAULT_PACKAGE;
    }
}
