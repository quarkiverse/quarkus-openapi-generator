package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.xapi.generator.deployment.codegen.XApiConstants;
import io.quarkiverse.xapi.generator.deployment.codegen.XApiGeneratorCodeGenBase;

public class OpenApiGeneratorYmlCodeGen extends XApiGeneratorCodeGenBase {

    public OpenApiGeneratorYmlCodeGen() {
        super(new OpenApiGeneratorCodeGenerator(), new OpenApiConstants(XApiConstants.YML));
    }

}
