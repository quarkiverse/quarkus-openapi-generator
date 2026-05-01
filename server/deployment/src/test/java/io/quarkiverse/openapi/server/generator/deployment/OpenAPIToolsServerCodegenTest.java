package io.quarkiverse.openapi.server.generator.deployment;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools.OpenAPIToolsServerCodegen;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;

class OpenAPIToolsServerCodegenTest {

    private static final Path WORK_DIR = Path.of("target/generated-test-sources");
    private static final Path INPUT_DIR = Path.of("src/test/resources");
    private static final String OUT_DIR = "target/generated-test-sources";

    @BeforeAll
    static void setup() throws IOException {
        FileUtils.deleteDirectory(new File("target/generated-test-sources/openapitools"));
        FileUtils.deleteDirectory(new File("target/generated-test-sources/openapitools-alias"));
    }

    @Test
    void shouldGenerateMultipleSpecsWithDifferentBasePackages() throws CodeGenException {
        Config config = MockConfigUtils.getTestConfig("openapitools-multispec.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, Path.of(OUT_DIR, "openapitools"), WORK_DIR,
                INPUT_DIR, false, config, true);

        new OpenAPIToolsServerCodegen().trigger(codeGenContext);

        assertTrue(Files.exists(
                Path.of("target/generated-test-sources/openapitools/org/acme/petstore/model/Pet.java")));
        assertTrue(Files.exists(
                Path.of("target/generated-test-sources/openapitools/org/acme/animal/model/Animal.java")));
    }

    @Test
    void shouldGenerateNamedSpecFromExplicitSpecProperty() throws CodeGenException {
        Config config = MockConfigUtils.getTestConfig("openapitools-alias.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, Path.of(OUT_DIR, "openapitools-alias"), WORK_DIR,
                INPUT_DIR, false, config, true);

        new OpenAPIToolsServerCodegen().trigger(codeGenContext);

        assertTrue(Files.exists(
                Path.of("target/generated-test-sources/openapitools-alias/org/acme/alias/model/Animal.java")));
    }
}
