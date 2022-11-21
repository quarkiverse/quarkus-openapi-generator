package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.openapi.generator.deployment.CodegenConfig;
import io.quarkiverse.spec.generator.deployment.codegen.SpecApiConstants;

public class OpenApiConstants extends SpecApiConstants {

    protected OpenApiConstants(String extension) {
        super("open-api", "openapi", "org.openapi.quarkus", CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX, extension);
    }
}
