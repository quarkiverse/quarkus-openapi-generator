package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.spec.generator.deployment.codegen.SpecApiGeneratorStreamCodeGen;
import io.quarkiverse.spec.generator.deployment.codegen.SpecApiParameters;

public class OpenApiGeneratorStreamCodeGen extends SpecApiGeneratorStreamCodeGen<OpenApiSpecInputProvider> {

    public OpenApiGeneratorStreamCodeGen() {
        super(new OpenApiGeneratorCodeGenerator(), new OpenApiParameters(SpecApiParameters.STREAM),
                OpenApiSpecInputProvider.class);
    }
}
