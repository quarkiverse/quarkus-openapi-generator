package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.spec.generator.deployment.codegen.SpecApiConstants;
import io.quarkiverse.spec.generator.deployment.codegen.SpecApiGeneratorCodeGenBase;

public class OpenApiGeneratorYmlCodeGen extends SpecApiGeneratorCodeGenBase {

    public OpenApiGeneratorYmlCodeGen() {
        super(new OpenApiGeneratorCodeGenerator(), new OpenApiConstants(SpecApiConstants.YML));
    }
}
