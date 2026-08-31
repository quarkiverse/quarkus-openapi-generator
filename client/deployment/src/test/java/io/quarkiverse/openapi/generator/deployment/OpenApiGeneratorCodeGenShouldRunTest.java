package io.quarkiverse.openapi.generator.deployment;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.quarkiverse.openapi.generator.common.OpenApiGeneratorOptions;
import io.quarkiverse.openapi.generator.deployment.codegen.OpenApiGeneratorCodeGenBase;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;

class OpenApiGeneratorCodeGenShouldRunTest {

    @TempDir
    Path projectDir;

    private final TestCodegen codegen = new TestCodegen();

    @Test
    void shouldRunForMainOpenApiDirectory() throws IOException {
        Path sourceDir = createDir("src", "main", "openapi");

        assertThat(codegen.shouldRun(sourceDir, config(Map.of()))).isTrue();
    }

    @Test
    void shouldRunForTestOpenApiDirectoryWhenInputBaseDirIsNotSet() throws IOException {
        Path sourceDir = createDir("src", "test", "openapi");

        assertThat(codegen.shouldRun(sourceDir, config(Map.of()))).isTrue();
    }

    @Test
    void shouldNotRunForOtherSourceRoot() throws IOException {
        Path sourceDir = createDir("target", "generated-sources", "openapi");

        assertThat(codegen.shouldRun(sourceDir, config(Map.of()))).isFalse();
    }

    @Test
    void shouldRunOnlyOnceWhenCustomInputBaseDirIsConfigured() throws IOException {
        createDir("mycustompath");

        Config config = config(Map.of(
                "quarkus.openapi-generator.codegen.input-base-dir", "mycustompath"));

        Path mainSourceDir = projectDir.resolve("src").resolve("main").resolve("openapi");
        Path testSourceDir = projectDir.resolve("src").resolve("test").resolve("openapi");

        assertThat(codegen.shouldRun(mainSourceDir, config)).isTrue();
        assertThat(codegen.shouldRun(testSourceDir, config)).isFalse();
    }

    private Path createDir(String... parts) throws IOException {
        Path dir = projectDir;
        for (String part : parts) {
            dir = dir.resolve(part);
        }
        Files.createDirectories(dir);
        return dir;
    }

    private static SmallRyeConfig config(Map<String, String> values) {
        SmallRyeConfigBuilder builder = new SmallRyeConfigBuilder();
        values.forEach(builder::withDefaultValue);
        return builder.build();
    }

    static final class TestCodegen extends OpenApiGeneratorCodeGenBase {
        @Override
        protected void doGenerate(OpenApiGeneratorOptions options) {
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
