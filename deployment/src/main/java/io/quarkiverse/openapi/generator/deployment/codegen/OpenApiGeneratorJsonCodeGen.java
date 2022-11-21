package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.spec.generator.deployment.codegen.SpecApiConstants;
import io.quarkiverse.spec.generator.deployment.codegen.SpecApiGeneratorCodeGenBase;

public class OpenApiGeneratorJsonCodeGen extends SpecApiGeneratorCodeGenBase {

    public OpenApiGeneratorJsonCodeGen() {
        super(new OpenApiGeneratorCodeGenerator(), new OpenApiConstants(SpecApiConstants.JSON));
    }
}
