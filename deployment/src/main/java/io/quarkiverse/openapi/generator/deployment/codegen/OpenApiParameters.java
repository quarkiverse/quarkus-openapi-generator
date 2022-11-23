package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.spec.generator.deployment.codegen.SpecParameters;

public class OpenApiParameters extends SpecParameters {

    protected OpenApiParameters(String extension) {
        super("open-api", "openapi", extension);
    }
}
