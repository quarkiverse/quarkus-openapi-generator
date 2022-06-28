package io.quarkiverse.openapi.generator.deployment.codegen;

public class OpenApiGeneratorYamlCodeGen extends OpenApiGeneratorCodeGenBase {

    @Override
    public String providerId() {
        return OpenApiGeneratorOutputPaths.YAML_PATH;
    }

    @Override
    public String inputExtension() {
        return YAML;
    }
}