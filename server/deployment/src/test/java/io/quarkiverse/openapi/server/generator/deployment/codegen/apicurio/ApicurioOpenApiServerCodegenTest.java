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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ApicurioOpenApiServerCodegenTest {

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
