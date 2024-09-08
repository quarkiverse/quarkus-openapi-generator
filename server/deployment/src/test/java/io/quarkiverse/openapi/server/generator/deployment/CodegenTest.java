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

import io.quarkiverse.openapi.server.generator.deployment.codegen.ApicurioOpenApiServerCodegen;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;

public class CodegenTest {

    private final static Path WORK_DIR = Path.of("target/generated-test-sources");
    private final static Path INPUT_DIR = Path.of("src/test/resources");
    private final static String OUT_DIR = "target/generated-test-sources";

    @BeforeAll
    public static void setup() throws IOException {
        FileUtils.deleteDirectory(new File("target/generated-test-sources"));
    }

    @Test
    public void testJSON() throws CodeGenException {
        Config config = MockConfigUtils.getTestConfig("json.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, Path.of(OUT_DIR, "json"), WORK_DIR,
                INPUT_DIR, false, config, true);
        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();
        apicurioOpenApiServerCodegen.trigger(codeGenContext);
        assertTrue(
                Files.exists(Path.of("target/generated-test-sources/json/io/petstore/PetResource.java")));
    }

    @Test
    public void testYaml() throws CodeGenException {
        Config config = MockConfigUtils.getTestConfig("yaml.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, Path.of(OUT_DIR, "yaml"), WORK_DIR,
                INPUT_DIR, false, config, true);
        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();
        apicurioOpenApiServerCodegen.trigger(codeGenContext);
        assertTrue(
                Files.exists(Path.of("target/generated-test-sources/yaml/io/petstore/PetResource.java")));
    }

    @Test
    public void testYamlHuge() throws CodeGenException {
        Config config = MockConfigUtils.getTestConfig("yaml-huge.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, Path.of(OUT_DIR, "yaml"), WORK_DIR,
                INPUT_DIR, false, config, true);
        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();
        apicurioOpenApiServerCodegen.trigger(codeGenContext);
        assertTrue(
                Files.exists(Path.of("target/generated-test-sources/yaml/io/petstore/PetResource.java")));
    }

    @Test
    public void testInputDir() throws CodeGenException {
        Config config = MockConfigUtils.getTestConfig("inputDir.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, Path.of(OUT_DIR, "inputDir"), WORK_DIR,
                INPUT_DIR, false, config, true);
        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();
        apicurioOpenApiServerCodegen.trigger(codeGenContext);
        assertTrue(Files.exists(
                Path.of("target/generated-test-sources/inputDir/io/petstore/PetResource.java")));
    }

}
