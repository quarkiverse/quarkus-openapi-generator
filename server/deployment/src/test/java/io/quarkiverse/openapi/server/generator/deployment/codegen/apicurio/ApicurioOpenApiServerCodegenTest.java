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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
    void should_apply_model_name_suffix_to_schemas_and_refs() throws Exception {
        String spec = """
                {
                  "openapi": "3.0.2",
                  "info": { "title": "Petstore", "version": "1.0.0" },
                  "paths": {
                    "/pet": {
                      "get": {
                        "operationId": "getPet",
                        "responses": {
                          "200": {
                            "description": "OK",
                            "content": {
                              "application/json": {
                                "schema": { "$ref": "#/components/schemas/Pet" }
                              }
                            }
                          }
                        }
                      }
                    }
                  },
                  "components": {
                    "schemas": {
                      "Pet": {
                        "type": "object",
                        "discriminator": {
                          "propertyName": "petType",
                          "mapping": { "cat": "#/components/schemas/Cat" }
                        },
                        "properties": {
                          "friend": { "$ref": "#/components/schemas/Cat" }
                        }
                      },
                      "Cat": { "type": "object" }
                    }
                  }
                }
                """;
        Path specPath = tempDir.resolve("petstore.json");
        Files.writeString(specPath, spec);
        Path outputPath = tempDir.resolve("petstore-suffixed.json");

        ApicurioOpenApiServerCodegen codegen = new ApicurioOpenApiServerCodegen();
        Method method = ApicurioOpenApiServerCodegen.class.getDeclaredMethod("applyModelNameSuffix", Path.class,
                String.class, Path.class);
        method.setAccessible(true);
        method.invoke(codegen, specPath, "Dto", outputPath);

        String json = Files.readString(outputPath);
        assertThat(json).contains("\"PetDto\"");
        assertThat(json).contains("\"CatDto\"");
        assertThat(json).contains("#/components/schemas/PetDto");
        assertThat(json).contains("#/components/schemas/CatDto");
        assertThat(json).doesNotContain("\"Pet\"");
        assertThat(json).doesNotContain("\"Cat\"");
        assertThat(json).doesNotContain("#/components/schemas/Pet\"");
        assertThat(json).doesNotContain("#/components/schemas/Cat\"");
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
