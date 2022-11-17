package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.codegen.OpenApiGeneratorCodeGenBase.*;

import io.quarkiverse.xapi.generator.deployment.codegen.XApiGeneratorStreamCodeGen;

public class OpenApiGeneratorStreamCodeGen extends XApiGeneratorStreamCodeGen<OpenApiSpecInputProvider> {

    public OpenApiGeneratorStreamCodeGen() {
        super(new OpenApiGeneratorCodeGenerator(), OpenApiSpecInputProvider.class);
    }

    @Override
    public String inputDirectory() {
        return INPUT_DIR;
    }

    @Override
    protected String getDefaultPackage() {
        return DEFAULT_PACKAGE;
    }

    @Override
    public String providerPrefix() {
        return PROVIDER_PREFIX;
    }
}
