package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.quarkiverse.openapi.generator.common.OpenApiGeneratorOptions;
import io.quarkiverse.openapi.server.generator.deployment.CodegenConfig;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import io.smallrye.config.common.utils.StringUtil;

class OpenAPIToolsServerCodegenSkipIfUnchangedTest {

    private static final String SPEC_YAML = "petstore.yaml";
    private static final String CONFIG_PROPERTY = "quarkus.openapi.generator.server.skip-if-unchanged";

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

        var codegen = new TestServerCodegen();
        var config = config(Map.of(CONFIG_PROPERTY, "true"));
        var options = buildOptions(config, spec, outDir);
        var configurator = minimalConfigurator(spec, outDir);

        codegen.generate(configurator, options);
        codegen.generate(configurator, options);

        assertThat(codegen.generateCount).hasValue(1);
    }

    @Test
    void shouldGenerateAgainWhenOptionDisabled() throws Exception {
        Path spec = tempDir.resolve(SPEC_YAML);
        Files.writeString(spec, SPEC);

        Path outDir = tempDir.resolve("generated");
        Files.createDirectories(outDir);

        var codegen = new TestServerCodegen();
        var config = config(Map.of(CONFIG_PROPERTY, "false"));
        var options = buildOptions(config, spec, outDir);
        var configurator = minimalConfigurator(spec, outDir);

        codegen.generate(configurator, options);
        codegen.generate(configurator, options);

        assertThat(codegen.generateCount).hasValue(2);
    }

    @Test
    void shouldGenerateAgainWhenSpecChanges() throws Exception {
        Path spec = tempDir.resolve(SPEC_YAML);
        Files.writeString(spec, SPEC);

        Path outDir = tempDir.resolve("generated");
        Files.createDirectories(outDir);

        var codegen = new TestServerCodegen();
        var config = config(Map.of(CONFIG_PROPERTY, "true"));
        var options = buildOptions(config, spec, outDir);
        var configurator = minimalConfigurator(spec, outDir);

        codegen.generate(configurator, options);

        // Change the spec to trigger a new generation
        Files.writeString(spec, ANOTHER_SPEC);
        codegen.generate(configurator, options);

        assertThat(codegen.generateCount).hasValue(2);
    }

    private OpenApiGeneratorOptions buildOptions(SmallRyeConfig config, Path spec, Path outDir) {
        return new OpenApiGeneratorOptions(
                getClass(),
                CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX,
                config,
                spec,
                StringUtil.replaceNonAlphanumericByUnderscores(spec.getFileName().toString()),
                outDir,
                null,
                false);
    }

    private static QuarkusJavaServerCodegenConfigurator minimalConfigurator(Path spec, Path outDir) {
        return new QuarkusJavaServerCodegenConfigurator()
                .withInputBaseDir(spec.toString())
                .withOutputDir(outDir.toAbsolutePath().toString());
    }

    private static SmallRyeConfig config(Map<String, String> values) {
        var builder = new SmallRyeConfigBuilder();
        values.forEach(builder::withDefaultValue);
        return builder.build();
    }

    /**
     * Test server codegen that counts invocations of {@link #doGenerate} instead of actually generating code.
     * Writes a dummy {@code .java} file so that {@link io.quarkiverse.openapi.generator.common.SkipGenerationSupport}
     * detects generated output on subsequent runs.
     */
    static final class TestServerCodegen extends OpenAPIToolsServerCodegen {

        final AtomicInteger generateCount = new AtomicInteger();

        @Override
        protected void doGenerate(OpenAPIToolsGenerator generator) {
            generateCount.incrementAndGet();
        }

        @Override
        protected void generate(QuarkusJavaServerCodegenConfigurator configurator, OpenApiGeneratorOptions options) {
            // Write a dummy Java file so SkipGenerationSupport.hasGeneratedFiles() returns true on the second run
            try {
                Path javaFile = options.outDir().resolve("org/example/PetResource.java");
                Files.createDirectories(javaFile.getParent());
                if (!Files.exists(javaFile)) {
                    Files.writeString(javaFile, "package org.example; interface PetResource {}");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            super.generate(configurator, options);
        }

        @Override
        public String providerId() {
            return "openapi-server-test";
        }
    }
}
