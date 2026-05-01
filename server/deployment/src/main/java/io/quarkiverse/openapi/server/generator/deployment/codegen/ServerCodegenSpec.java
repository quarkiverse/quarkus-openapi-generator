package io.quarkiverse.openapi.server.generator.deployment.codegen;

import java.nio.file.Path;

public record ServerCodegenSpec(
        String configKey,
        Path inputBaseDir,
        Path specPath,
        String basePackage,
        boolean reactive,
        boolean builders,
        boolean beanValidation) {
}
