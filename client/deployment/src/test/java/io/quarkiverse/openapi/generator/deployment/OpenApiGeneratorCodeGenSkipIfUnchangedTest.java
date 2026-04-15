package io.quarkiverse.openapi.generator.deployment;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getSanitizedFileName;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.quarkiverse.openapi.generator.common.OpenApiGeneratorOptions;
import io.quarkiverse.openapi.generator.deployment.codegen.OpenApiGeneratorCodeGenBase;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;

class OpenApiGeneratorCodeGenSkipIfUnchangedTest {

    private static final String SPEC_YAML = "petstore.yaml";
    private static final String CONFIG_PROPERTY = "quarkus.openapi-generator.codegen.skip-if-unchanged";

    private static final String SPEC = """
            openapi: 3.0.1
            info:
              title: Test
              version: 1.0.0
            paths: {}""";

    private static final String ANOTHER_SPEC = """
            openapi: 3.0.1
            info:
              title: Another Test
              version: 1.0.0
            paths: {}""";

    @TempDir
    Path tempDir;

    @Test
    void shouldSkipGenerationWhenEnabledAndUnchanged() throws Exception {
        Path spec = tempDir.resolve(SPEC_YAML);
        Files.writeString(spec, SPEC);

        Path outDir = tempDir.resolve("generated");
        Files.createDirectories(outDir);

        TestCodegen codegen = new TestCodegen();
        Config config = config(Map.of(CONFIG_PROPERTY, "true"));

        OpenApiGeneratorOptions options = new OpenApiGeneratorOptions(
                CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX,
                config,
                spec,
                getSanitizedFileName(spec),
                outDir,
                tempDir.resolve("templates"),
                false);

        codegen.generate(options);
        codegen.generate(options);

        assertThat(codegen.generateCount).hasValue(1);
    }

    @Test
    void shouldGenerateAgainWhenOptionDisabled() throws Exception {
        Path spec = tempDir.resolve(SPEC_YAML);
        Files.writeString(spec, SPEC);

        Path outDir = tempDir.resolve("generated");
        Files.createDirectories(outDir);

        TestCodegen codegen = new TestCodegen();
        Config config = config(Map.of(CONFIG_PROPERTY, "false"));

        OpenApiGeneratorOptions options = new OpenApiGeneratorOptions(
                CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX,
                config,
                spec,
                getSanitizedFileName(spec),
                outDir,
                tempDir.resolve("templates"),
                false);

        codegen.generate(options);
        codegen.generate(options);

        assertThat(codegen.generateCount).hasValue(2);
    }

    @Test
    void shouldGenerateAgainWhenFingerprintChanges() throws Exception {
        Path spec = tempDir.resolve(SPEC_YAML);
        Files.writeString(spec, SPEC);

        Path outDir = tempDir.resolve("generated");
        Files.createDirectories(outDir);

        TestCodegen codegen = new TestCodegen();
        Config config = config(Map.of(CONFIG_PROPERTY, "true"));

        OpenApiGeneratorOptions options = new OpenApiGeneratorOptions(
                CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX,
                config,
                spec,
                getSanitizedFileName(spec),
                outDir,
                tempDir.resolve("templates"),
                false);
        codegen.generate(options);

        // Change the spec, to trigger a new generation
        Files.writeString(spec, ANOTHER_SPEC);
        codegen.generate(options);

        assertThat(codegen.generateCount).hasValue(2);
    }

    @Test
    void shouldGenerateAgainWhenConfigKeyChanges() throws Exception {
        Path spec = tempDir.resolve(SPEC_YAML);
        Files.writeString(spec, SPEC);

        Path outDir = tempDir.resolve("generated");
        Files.createDirectories(outDir);

        TestCodegen codegen = new TestCodegen();

        Config firstConfig = config(Map.of(
                CONFIG_PROPERTY, "true",
                "quarkus.openapi-generator.codegen.spec.petstore_yaml.config-key", "petstore",
                "quarkus.openapi-generator.codegen.spec.petstore.additional-api-type-annotations", "@org.test.Foo"));

        OpenApiGeneratorOptions firstOptions = new OpenApiGeneratorOptions(
                CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX,
                firstConfig,
                spec,
                getSanitizedFileName(spec),
                outDir,
                tempDir.resolve("templates"),
                false);

        codegen.generate(firstOptions);

        Config secondConfig = config(Map.of(
                CONFIG_PROPERTY, "true",
                "quarkus.openapi-generator.codegen.spec.petstore_yaml.config-key", "another",
                "quarkus.openapi-generator.codegen.spec.another.additional-api-type-annotations", "@org.test.Foo"));

        OpenApiGeneratorOptions secondOptions = new OpenApiGeneratorOptions(
                CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX,
                secondConfig,
                spec,
                getSanitizedFileName(spec),
                outDir,
                tempDir.resolve("templates"),
                false);

        codegen.generate(secondOptions);

        assertThat(codegen.generateCount).hasValue(2);
    }

    private static SmallRyeConfig config(Map<String, String> values) {
        SmallRyeConfigBuilder builder = new SmallRyeConfigBuilder();
        values.forEach(builder::withDefaultValue);
        return builder.build();
    }

    // Test code generator that generates dummy content
    static final class TestCodegen extends OpenApiGeneratorCodeGenBase {
        final AtomicInteger generateCount = new AtomicInteger();

        @Override
        protected void doGenerate(OpenApiGeneratorOptions options) {
            generateCount.incrementAndGet();
            try {
                Path javaFile = options.outDir().resolve("org/example/PetApi.java");
                Files.createDirectories(javaFile.getParent());
                Files.writeString(javaFile, "package org.example; class PetApi {}");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void generate(final OpenApiGeneratorOptions options) {
            super.generate(options);
        }

        @Override
        public String providerId() {
            return "openapi-test";
        }

        @Override
        public String[] inputExtensions() {
            return new String[] { ".yaml" };
        }
    }
}
