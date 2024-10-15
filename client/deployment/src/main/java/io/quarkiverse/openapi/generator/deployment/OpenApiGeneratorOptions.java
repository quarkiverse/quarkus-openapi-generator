package io.quarkiverse.openapi.generator.deployment;

import java.nio.file.Path;

import org.eclipse.microprofile.config.Config;

public record OpenApiGeneratorOptions(
        Config config,
        Path openApiFilePath,
        Path outDir,
        Path templateDir,
        boolean isRestEasyReactive) {
}
