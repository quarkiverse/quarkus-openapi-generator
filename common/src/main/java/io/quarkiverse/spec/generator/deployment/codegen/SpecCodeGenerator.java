package io.quarkiverse.spec.generator.deployment.codegen;

import java.nio.file.Path;

import org.eclipse.microprofile.config.Config;

public interface SpecCodeGenerator {

    void generate(final Config config, final Path openApiFilePath, final Path outDir, final String basePackage);

}
