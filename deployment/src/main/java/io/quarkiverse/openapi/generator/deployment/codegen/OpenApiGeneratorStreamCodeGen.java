package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.xapi.generator.deployment.codegen.XApiConstants;
import io.quarkiverse.xapi.generator.deployment.codegen.XApiGeneratorStreamCodeGen;

public class OpenApiGeneratorStreamCodeGen extends XApiGeneratorStreamCodeGen<OpenApiSpecInputProvider> {

    public OpenApiGeneratorStreamCodeGen() {
        super(new OpenApiGeneratorCodeGenerator(), new OpenApiConstants(XApiConstants.STREAM), OpenApiSpecInputProvider.class);
    }
}
