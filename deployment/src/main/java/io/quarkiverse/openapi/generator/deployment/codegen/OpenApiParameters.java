package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.spec.generator.deployment.codegen.SpecApiParameters;

public class OpenApiParameters extends SpecApiParameters {

    protected OpenApiParameters(String extension) {
        super("open-api", "openapi", extension);
    }
}
