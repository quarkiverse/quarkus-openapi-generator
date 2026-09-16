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

import io.quarkiverse.openapi.server.generator.deployment.codegen.ServerCodegenSpec;

class ApicurioOpenApiServerCodegenTest {

    @TempDir
    Path tempDir;

    @Test
    void should_resolve_external_refs_into_json() throws Exception {
        Path specPath = findSpec("openapi/multifile/module1.yaml");

        ApicurioOpenApiServerCodegen codegen = new ApicurioOpenApiServerCodegen();
        Method method = ApicurioOpenApiServerCodegen.class.getDeclaredMethod("resolveToJSON", Path.class);
        method.setAccessible(true);

        File jsonFile = (File) method.invoke(codegen, specPath);
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
        Method method = ApicurioOpenApiServerCodegen.class.getDeclaredMethod("resolveToJSON", Path.class);
        method.setAccessible(true);

        File jsonFile = (File) method.invoke(codegen, specPath);
        String json = Files.readString(jsonFile.toPath());

        assertThat(json).contains("\"Something\"");
    }

    @Test
    @DisplayName("Should keep local $refs so scalar schema path params are generated as primitives (issue #1784)")
    void should_keep_local_refs_for_scalar_schema_path_params() throws Exception {
        Path specPath = findSpec(
                "io/quarkiverse/openapi/server/generator/deployment/codegen/apicurio/scalar-schema-path-param.yaml");

        ApicurioOpenApiServerCodegen codegen = new ApicurioOpenApiServerCodegen();
        Method method = ApicurioOpenApiServerCodegen.class.getDeclaredMethod("resolveToJSON", Path.class);
        method.setAccessible(true);

        File jsonFile = (File) method.invoke(codegen, specPath);
        String json = Files.readString(jsonFile.toPath());
        assertThat(json).contains("\"$ref\":\"#/components/schemas/Int64Id\"");

        Path outDir = tempDir.resolve("out");
        ServerCodegenSpec spec = new ServerCodegenSpec("test", specPath.getParent(), specPath, "org.acme", false, false,
                true);
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
