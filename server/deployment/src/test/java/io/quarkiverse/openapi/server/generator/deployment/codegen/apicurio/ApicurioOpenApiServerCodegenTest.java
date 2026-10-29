package io.quarkiverse.openapi.server.generator.deployment.codegen.apicurio;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkiverse.openapi.server.generator.deployment.codegen.ServerCodegenSpec;

class ApicurioOpenApiServerCodegenTest {

    @TempDir
    Path tempDir;

    @Test
    void should_resolve_external_refs_into_json() throws Exception {
        Path specPath = findSpec("openapi/multifile/module1.yaml");

        ApicurioOpenApiServerCodegen codegen = new ApicurioOpenApiServerCodegen();
        Method method = ApicurioOpenApiServerCodegen.class.getDeclaredMethod("resolveToJSON", Path.class,
                boolean.class);
        method.setAccessible(true);

        File jsonFile = (File) method.invoke(codegen, specPath, false);
        String json = Files.readString(jsonFile.toPath());

        assertThat(json).contains("\"CommonPet\"");
        assertThat(json).doesNotContain("common-spec.yaml");
    }

    @Test
    @DisplayName("Should handle self-referencing schema without StackOverflowError")
    void should_handle_self_referencing_schema() throws Exception {
        Path specPath = findSpec(
                "io/quarkiverse/openapi/server/generator/deployment/codegen/apicurio/self-referencing-schema.json");

        ApicurioOpenApiServerCodegen codegen = new ApicurioOpenApiServerCodegen();
        Method method = ApicurioOpenApiServerCodegen.class.getDeclaredMethod("resolveToJSON", Path.class,
                boolean.class);
        method.setAccessible(true);

        File jsonFile = (File) method.invoke(codegen, specPath, false);
        String json = Files.readString(jsonFile.toPath());

        assertThat(json).contains("\"Something\"");
    }

    @ParameterizedTest(name = "keepLocalRefs={0}")
    @ValueSource(booleans = { false, true })
    @DisplayName("Should generate scalar schema path params as primitives (issue #1784)")
    void should_generate_scalar_schema_path_params_as_primitives(boolean keepLocalRefs) throws Exception {
        Path specPath = findSpec(
                "io/quarkiverse/openapi/server/generator/deployment/codegen/apicurio/scalar-schema-path-param.yaml");

        ApicurioOpenApiServerCodegen codegen = new ApicurioOpenApiServerCodegen();
        Method method = ApicurioOpenApiServerCodegen.class.getDeclaredMethod("resolveToJSON", Path.class,
                boolean.class);
        method.setAccessible(true);

        File jsonFile = (File) method.invoke(codegen, specPath, keepLocalRefs);

        Path outDir = tempDir.resolve("out");
        ServerCodegenSpec spec = ServerCodegenSpec.builder()
                .configKey("test")
                .inputBaseDir(specPath.getParent())
                .specPath(specPath)
                .basePackage("org.acme")
                .beanValidation(true)
                .build();
        new ApicurioCodegenWrapper(outDir.toFile(), spec).generate(jsonFile.toPath());

        Path resourceFile = outDir.resolve(Path.of("org", "acme", "ItemsResource.java"));
        String resource = Files.readString(resourceFile);
        assertThat(resource).contains("long itemId");
        assertThat(resource).doesNotContain("Int64Id");
        try (Stream<Path> files = Files.walk(outDir)) {
            assertThat(files.filter(Files::isRegularFile)).containsExactly(resourceFile);
        }
    }

    private Path findSpec(String resourcePath) {
        URL url = this.getClass().getResource("/" + resourcePath);
        Objects.requireNonNull(url, "Could not find /" + resourcePath);

        URI uri;
        try {
            uri = url.toURI();
        } catch (Exception e) {
            throw new RuntimeException("Invalid URI for " + url, e);
        }
        return Paths.get(uri);
    }
}
