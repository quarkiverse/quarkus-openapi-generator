package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.xapi.generator.deployment.codegen.XApiConstants;
import io.quarkiverse.xapi.generator.deployment.codegen.XApiGeneratorCodeGenBase;

public class OpenApiGeneratorYamlCodeGen extends XApiGeneratorCodeGenBase {

    public OpenApiGeneratorYamlCodeGen() {
        super(new OpenApiGeneratorCodeGenerator(), new OpenApiConstants(XApiConstants.YAML));
    }
}