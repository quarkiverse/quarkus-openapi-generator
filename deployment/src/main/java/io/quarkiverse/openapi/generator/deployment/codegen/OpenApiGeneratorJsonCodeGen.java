package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.xapi.generator.deployment.codegen.XApiConstants;
import io.quarkiverse.xapi.generator.deployment.codegen.XApiGeneratorCodeGenBase;

public class OpenApiGeneratorJsonCodeGen extends XApiGeneratorCodeGenBase {

    public OpenApiGeneratorJsonCodeGen() {
        super(new OpenApiGeneratorCodeGenerator(), new OpenApiConstants(XApiConstants.JSON));
    }
}
