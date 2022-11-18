package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.openapi.generator.deployment.CodegenConfig;
import io.quarkiverse.xapi.generator.deployment.codegen.XApiConstants;

public class OpenApiConstants extends XApiConstants {

    protected OpenApiConstants(String extension) {
        super("open-api", "openapi", "org.openapi.quarkus", CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX, extension);
    }
}
