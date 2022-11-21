package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.spec.generator.deployment.codegen.SpecApiConstants;
import io.quarkiverse.spec.generator.deployment.codegen.SpecApiGeneratorStreamCodeGen;

public class OpenApiGeneratorStreamCodeGen extends SpecApiGeneratorStreamCodeGen<OpenApiSpecInputProvider> {

    public OpenApiGeneratorStreamCodeGen() {
        super(new OpenApiGeneratorCodeGenerator(), new OpenApiConstants(SpecApiConstants.STREAM),
                OpenApiSpecInputProvider.class);
    }
}
