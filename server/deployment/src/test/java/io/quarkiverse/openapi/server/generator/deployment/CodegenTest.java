package io.quarkiverse.openapi.server.generator.deployment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import io.quarkiverse.openapi.server.generator.deployment.codegen.apicurio.ApicurioOpenApiServerCodegen;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;

public class CodegenTest {

    private static final Path WORK_DIR = Path.of("target/generated-test-sources");
    private static final Path INPUT_DIR = Path.of("src/test/resources");
    private static final String OUT_DIR = "target/generated-test-sources";

    @BeforeAll
    public static void setup() throws IOException {
        Files.createDirectories(Path.of(OUT_DIR));
    }

    private static Path outDir(String name) throws IOException {
        Path dir = Path.of(OUT_DIR, name);
        Files.createDirectories(dir);
        return dir;
    }

    @AfterAll
    public static void cleanup() throws IOException {
        FileUtils.deleteDirectory(new File(OUT_DIR));
    }

    @Test
    public void testJSON() throws CodeGenException, IOException {
        Config config = MockConfigUtils.getTestConfig("json.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, outDir("json"), WORK_DIR,
                INPUT_DIR, false, config, true);
        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();
        apicurioOpenApiServerCodegen.trigger(codeGenContext);
        assertTrue(
                Files.exists(Path.of("target/generated-test-sources/json/io/petstore/PetResource.java")));
    }

    @Test
    public void testYaml() throws CodeGenException, IOException {
        Config config = MockConfigUtils.getTestConfig("yaml.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, outDir("yaml"), WORK_DIR,
                INPUT_DIR, false, config, true);
        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();
        apicurioOpenApiServerCodegen.trigger(codeGenContext);
        assertTrue(
                Files.exists(Path.of("target/generated-test-sources/yaml/io/petstore/PetResource.java")));
    }

    @Test
    public void testInputDir() throws CodeGenException, IOException {
        Config config = MockConfigUtils.getTestConfig("inputDir.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, outDir("inputDir"), WORK_DIR,
                INPUT_DIR, false, config, true);
        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();
        apicurioOpenApiServerCodegen.trigger(codeGenContext);
        assertTrue(Files.exists(
                Path.of("target/generated-test-sources/inputDir/io/petstore/PetResource.java")));
    }

    @Test
    void testDuplicateOperationIdThrowsCodeGenException() throws IOException {
        Config config = MockConfigUtils.getTestConfig("duplicate-operation-id.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, outDir("duplicate-operation-id"), WORK_DIR,
                INPUT_DIR, false, config, true);
        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();
        Assertions.assertThrows(CodeGenException.class, () -> apicurioOpenApiServerCodegen.trigger(codeGenContext));
    }

    @Test
    public void shouldGenerateAnErrorWhenInputDirIsNotExist() throws IOException {
        Config config = MockConfigUtils.getTestConfig("doesNotExistDir.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, outDir("inputDir"), WORK_DIR,
                INPUT_DIR, false, config, true);
        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();

        Assertions.assertThrows(CodeGenException.class, () -> apicurioOpenApiServerCodegen.trigger(codeGenContext));
    }

    /**
     * Tests that a simple multipart/form-data request body with a binary file field generates
     * the correct JAX-RS resource with {@code @RestForm} and {@code FileUpload} parameter.
     */
    @Test
    void testMultipartFormContent() throws CodeGenException, IOException {
        Config config = MockConfigUtils.getTestConfig("multipart-form-content.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, outDir("multipart-form-content"), WORK_DIR,
                INPUT_DIR, false, config, true);
        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();
        apicurioOpenApiServerCodegen.trigger(codeGenContext);

        Path generatedFile = Path.of("target/generated-test-sources/multipart-form-content/io/example/api/FilesResource.java");
        assertTrue(Files.exists(generatedFile));

        CompilationUnit cu = StaticJavaParser.parse(generatedFile.toFile());
        MethodDeclaration postMethod = cu.findAll(MethodDeclaration.class).stream()
                .filter(method -> method.getAnnotationByName("POST").isPresent())
                .findFirst()
                .orElseThrow(() -> new AssertionError("No @POST method found"));

        assertThat(postMethod.getAnnotationByName("Consumes"))
                .isPresent()
                .hasValueSatisfying(annotation -> assertThat(annotation.toString()).contains("MULTIPART_FORM_DATA"));

        Parameter fileParam = postMethod.getParameters().stream()
                .filter(parameter -> parameter.getAnnotationByName("RestForm").isPresent())
                .findFirst()
                .orElseThrow(() -> new AssertionError("No @RestForm parameter found"));
        assertThat(fileParam.getTypeAsString()).isEqualTo("FileUpload");
    }

    /**
     * Tests that a multipart/form-data spec where all fields are arrays containing object
     * references generates correct {@code List<T>} parameters.
     */
    @Test
    void testMultipartArrayOnlyRef() throws CodeGenException, IOException {
        Config config = MockConfigUtils.getTestConfig("multipart-array-only-ref.application.properties");
        CodeGenContext codeGenContext = new CodeGenContext(null, outDir("multipart-array-only-ref"), WORK_DIR,
                INPUT_DIR, false, config, true);
        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();
        apicurioOpenApiServerCodegen.trigger(codeGenContext);

        Path generatedFile = Path
                .of("target/generated-test-sources/multipart-array-only-ref/io/example/api/UploadResource.java");
        assertTrue(Files.exists(generatedFile));

        CompilationUnit cu = StaticJavaParser.parse(generatedFile.toFile());
        MethodDeclaration postMethod = cu.findAll(MethodDeclaration.class).stream()
                .filter(method -> method.getAnnotationByName("POST").isPresent())
                .findFirst()
                .orElseThrow(() -> new AssertionError("No @POST method found"));

        assertThat(postMethod.getAnnotationByName("Consumes"))
                .isPresent()
                .hasValueSatisfying(annotation -> assertThat(annotation.toString()).contains("MULTIPART_FORM_DATA"));

        Parameter itemsParam = postMethod.getParameters().stream()
                .filter(parameter -> parameter.getAnnotationByName("RestForm")
                        .map(annotation -> annotation.toString().contains("\"items\""))
                        .orElse(false))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No @RestForm(\"items\") parameter found"));
        assertThat(itemsParam.getTypeAsString()).startsWith("List<");

        Parameter amountsParam = postMethod.getParameters().stream()
                .filter(parameter -> parameter.getAnnotationByName("RestForm")
                        .map(annotation -> annotation.toString().contains("\"amounts\""))
                        .orElse(false))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No @RestForm(\"amounts\") parameter found"));
        assertThat(amountsParam.getTypeAsString()).startsWith("List<");
    }

}
