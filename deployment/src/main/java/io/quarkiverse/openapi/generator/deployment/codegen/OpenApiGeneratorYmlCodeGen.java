package io.quarkiverse.openapi.generator.deployment.codegen;

public class OpenApiGeneratorYmlCodeGen extends OpenApiGeneratorCodeGenBase {

    @Override
    public String providerId() {
        return OpenApiGeneratorOutputPaths.YML_PATH;
    }

    @Override
    public String inputExtension() {
        return YML;
    }
}