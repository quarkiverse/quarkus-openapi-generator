package io.quarkiverse.openapi.generator.deployment.codegen;

public class OpenApiGeneratorJsonCodeGen extends OpenApiGeneratorCodeGenBase {

    @Override
    public String providerId() {
        return OpenApiGeneratorOutputPaths.JSON_PATH;
    }

    @Override
    public String inputExtension() {
        return JSON;
    }
}
