package io.quarkiverse.openapi.server.generator.deployment;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools.OpenAPIToolsServerCodegen;

class OpenAPIToolsServerCodegenShouldRunTest {

    private final OpenAPIToolsServerCodegen codegen = new OpenAPIToolsServerCodegen();

    @Test
    void shouldRunForMainResourcesDirectory() {
        Config config = MockConfigUtils.getTestConfig("openapitools-multispec.application.properties");
        Path sourceDir = Path.of("project", "src", "main", "resources");

        assertThat(codegen.shouldRun(sourceDir, config)).isTrue();
    }

    @Test
    void shouldNotRunForTestResourcesDirectory() {
        Config config = MockConfigUtils.getTestConfig("openapitools-multispec.application.properties");
        Path sourceDir = Path.of("project", "src", "test", "resources");

        assertThat(codegen.shouldRun(sourceDir, config)).isFalse();
    }

    @Test
    void shouldNotRunForAdditionalSourceRoot() {
        Config config = MockConfigUtils.getTestConfig("openapitools-multispec.application.properties");
        Path sourceDir = Path.of("project", "target", "generated-sources", "resources");

        assertThat(codegen.shouldRun(sourceDir, config)).isFalse();
    }

    @Test
    void shouldNotRunForCustomSourceRoot() {
        Config config = MockConfigUtils.getTestConfig("openapitools-multispec.application.properties");
        Path sourceDir = Path.of("project", "src", "generated", "resources");

        assertThat(codegen.shouldRun(sourceDir, config)).isFalse();
    }
}
