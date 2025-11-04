package io.quarkiverse.openapi.generator.deployment.codegen;

public class OpenApiGeneratorCodeGen extends OpenApiGeneratorCodeGenBase {
    @Override
    public String providerId() {
        return OpenApiGeneratorOutputPaths.OPENAPI_PATH;
    }

    @Override
    public String[] inputExtensions() {
        return SUPPORTED_EXTENSIONS_WITH_LEADING_DOT.toArray(new String[0]);
    }
}
