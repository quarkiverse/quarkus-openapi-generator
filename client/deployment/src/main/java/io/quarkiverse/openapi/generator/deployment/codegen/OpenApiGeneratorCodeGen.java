package io.quarkiverse.openapi.generator.deployment.codegen;

public class OpenApiGeneratorCodeGen extends OpenApiGeneratorCodeGenBase {
    @Override
    public String providerId() {
        return OpenApiGeneratorOutputPaths.OPENAPI_PATH;
    }

    @Override
    public String[] inputExtensions() {
        return new String[] { JSON, YAML, YML };
    }
}
