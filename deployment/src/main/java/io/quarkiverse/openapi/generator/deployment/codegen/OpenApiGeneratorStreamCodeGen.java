package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.spec.generator.deployment.codegen.SpecGeneratorStreamCodeGen;
import io.quarkiverse.spec.generator.deployment.codegen.SpecParameters;

public class OpenApiGeneratorStreamCodeGen extends SpecGeneratorStreamCodeGen<OpenApiSpecInputProvider> {

    public OpenApiGeneratorStreamCodeGen() {
        super(new OpenApiGeneratorCodeGenerator(), new OpenApiParameters(SpecParameters.STREAM),
                OpenApiSpecInputProvider.class);
    }
}
