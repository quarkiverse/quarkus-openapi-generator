package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.spec.generator.deployment.codegen.SpecApiConstants;
import io.quarkiverse.spec.generator.deployment.codegen.SpecApiGeneratorCodeGenBase;

public class OpenApiGeneratorYamlCodeGen extends SpecApiGeneratorCodeGenBase {

    public OpenApiGeneratorYamlCodeGen() {
        super(new OpenApiGeneratorCodeGenerator(), new OpenApiConstants(SpecApiConstants.YAML));
    }
}