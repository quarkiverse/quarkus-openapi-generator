package io.quarkiverse.openapi.generator.common;

import java.nio.file.Path;

import org.eclipse.microprofile.config.Config;

public record OpenApiGeneratorOptions(
        String codegenConfigPrefix,
        Config config,
        Path openApiFilePath,
        String sanitizedFileName,
        Path outDir,
        Path templateDir,
        boolean isRestEasyReactive) {
}
