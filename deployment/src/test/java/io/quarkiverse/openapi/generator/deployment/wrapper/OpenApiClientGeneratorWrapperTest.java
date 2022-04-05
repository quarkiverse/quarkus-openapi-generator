package io.quarkiverse.openapi.generator.deployment.wrapper;

import static io.quarkiverse.openapi.generator.deployment.assertions.Assertions.assertThat;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;


import io.quarkiverse.openapi.generator.annotations.GeneratedClass;
import io.quarkiverse.openapi.generator.annotations.GeneratedMethod;

import io.quarkiverse.openapi.generator.deployment.MockConfigUtils;
import io.quarkiverse.openapi.generator.deployment.codegen.ModelCodegenConfigParser;


public class OpenApiClientGeneratorWrapperTest {

    @Test
    void verifyCommonGenerated() throws URISyntaxException {
        final List<File> generatedFiles = createGeneratorWrapper("petstore-openapi.json").generate();
        assertNotNull(generatedFiles);
        assertFalse(generatedFiles.isEmpty());
    }

    @Test
    void verifyAuthBasicGenerated() throws URISyntaxException {
        final List<File> generatedFiles = createGeneratorWrapper("petstore-openapi-httpbasic.json").generate();
        assertTrue(generatedFiles.stream().anyMatch(f -> f.getName().equals("CompositeAuthenticationProvider.java")));
    }

    @Test
    void verifyAuthBearerGenerated() throws URISyntaxException {
        final List<File> generatedFiles = createGeneratorWrapper("petstore-openapi-bearer.json").generate();
        assertTrue(generatedFiles.stream().anyMatch(f -> f.getName().equals("CompositeAuthenticationProvider.java")));
    }

    @Test
    void verifyEnumGeneration() throws URISyntaxException, FileNotFoundException {
        final List<File> generatedFiles = createGeneratorWrapper("issue-28.yaml")
                .withBasePackage("org.issue28")
                .generate();
        final Optional<File> enumFile = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("ConnectorNamespaceState.java")).findFirst();
        assertThat(enumFile).isPresent();

        final CompilationUnit cu = StaticJavaParser.parse(enumFile.orElseThrow());
        final List<EnumConstantDeclaration> constants = cu.findAll(EnumConstantDeclaration.class);
        assertThat(constants)
                .hasSize(3)
                .extracting(EnumConstantDeclaration::getNameAsString)
                .containsExactlyInAnyOrder("DISCONNECTED", "READY", "DELETING");
    }

    @Test
    void verifyDeprecatedFields() throws URISyntaxException, FileNotFoundException {
        final Map<String, Object> codegenConfig = ModelCodegenConfigParser
                .parse(MockConfigUtils.getTestConfig("/codegen/application.properties"), "org.issue38");
        final List<File> generatedFiles = this.createGeneratorWrapper("issue-38.yaml")
                .withBasePackage("org.issue38")
                .withModelCodeGenConfiguration(codegenConfig)
                .generate();

        // we have two attributes that will be generated with the same name, one of them is deprecated
        final Optional<File> metaV1Condition = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("MetaV1Condition.java")).findFirst();
        assertThat(metaV1Condition).isPresent();
        final CompilationUnit cu = StaticJavaParser.parse(metaV1Condition.orElseThrow());
        final List<FieldDeclaration> fields = cu.findAll(FieldDeclaration.class);

        assertThat(fields).extracting(FieldDeclaration::getVariables).hasSize(5);

        assertThat(fields.stream()
                .flatMap(v -> v.getVariables().stream())
                .anyMatch(f -> f.getNameAsString().equals("lastTransitionTime")))
                .isTrue();

        // this one we optionally removed the deprecated attribute
        final Optional<File> connectorDeploymentSpec = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("ConnectorDeploymentSpec.java")).findFirst();
        assertThat(connectorDeploymentSpec).isPresent();
        final CompilationUnit cu2 = StaticJavaParser.parse(connectorDeploymentSpec.orElseThrow());
        final List<FieldDeclaration> fields2 = cu2.findAll(FieldDeclaration.class);

        assertThat(fields2.stream()
                .flatMap(v -> v.getVariables().stream())
                .anyMatch(f -> f.getNameAsString().equals("allowUpgrade")))
                .isFalse();

        //TODO: add a condition where we know there is a deprecated attribute and verify it's been generated
    }

    @Test
    void checkAnnotations() throws URISyntaxException, FileNotFoundException {
        List<File> restClientFiles = generateRestClientFiles();

        assertNotNull(restClientFiles);
        assertFalse(restClientFiles.isEmpty());

        Optional<File> file = restClientFiles.stream()
                .filter(f -> f.getName().endsWith("DefaultApi.java"))
                .findAny();

        assertThat(file).isNotEmpty();

        CompilationUnit compilationUnit = StaticJavaParser.parse(file.orElseThrow());

        compilationUnit.findAll(ClassOrInterfaceDeclaration.class)
                .forEach(c -> assertThat(c.getAnnotationByClass(GeneratedClass.class)).isPresent());

        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();

        methodDeclarations.forEach(m -> assertThat(m.getAnnotationByClass(GeneratedMethod.class)).isPresent());

        Optional<MethodDeclaration> byeMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals("byeGet"))
                .findAny();

        assertThat(byeMethod).isNotEmpty();

        assertThat(byeMethod.orElseThrow())
                .hasCircuitBreakerAnnotation()
                .doesNotHaveAnyCircuitBreakerAttribute();

        methodDeclarations.stream()
                .filter(m -> !m.getNameAsString().equals("byeGet"))
                .forEach(m -> assertThat(m).doesNotHaveCircuitBreakerAnnotation());
    }

    private List<File> generateRestClientFiles() throws URISyntaxException {
        OpenApiClientGeneratorWrapper generatorWrapper = createGeneratorWrapper("simple-openapi.json")
                .withCircuitBreakerConfiguration(Map.of(
                        "org.openapitools.client.api.DefaultApi", List.of("opThatDoesNotExist", "byeGet")));

        return generatorWrapper.generate();
    }

    @Test
    void verifyMultipartFormAnnotationIsGeneratedForParameter() throws URISyntaxException, FileNotFoundException {
        List<File> generatedFiles = createGeneratorWrapper("multipart-openapi.yml")
                .withSkipFormModelConfig("false")
                .generate();
        assertThat(generatedFiles).isNotEmpty();

        Optional<File> file = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("UserProfileDataApi.java"))
                .findAny();
        assertThat(file).isPresent();

        CompilationUnit compilationUnit = StaticJavaParser.parse(file.orElseThrow());
        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();

        Optional<MethodDeclaration> multipartPostMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals("postUserProfileData"))
                .findAny();
        assertThat(multipartPostMethod).isPresent();

        List<Parameter> parameters = multipartPostMethod.orElseThrow().getParameters();
        assertThat(parameters).hasSize(1);

        Parameter param = parameters.get(0);
        assertThat(param.getAnnotationByName("MultipartForm")).isPresent();
    }

    @Test
    void verifyMultipartPojoGeneratedAndFieldsHaveAnnotations() throws URISyntaxException, FileNotFoundException {
        List<File> generatedFiles = createGeneratorWrapper("multipart-openapi.yml")
                .withSkipFormModelConfig("false")
                .generate();
        assertFalse(generatedFiles.isEmpty());

        Optional<File> file = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("UserProfileDataApi.java"))
                .findAny();
        assertThat(file).isNotEmpty();

        CompilationUnit compilationUnit = StaticJavaParser.parse(file.orElseThrow());
        Optional<ClassOrInterfaceDeclaration> multipartPojo = compilationUnit.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .filter(c -> c.getNameAsString().equals("PostUserProfileDataMultipartForm"))
                .findAny();
        assertThat(multipartPojo).isNotEmpty();

        assertThat(multipartPojo.orElseThrow().getFields()).hasSize(3);
        multipartPojo.orElseThrow().getFields().forEach(field -> {
            assertThat(field.getAnnotationByName("FormParam")).isPresent();
            assertThat(field.getAnnotationByName("PartType")).isPresent();
        });
    }

    private OpenApiClientGeneratorWrapper createGeneratorWrapper(String specFileName) throws URISyntaxException {
        final Path openApiSpec = Path
                .of(requireNonNull(this.getClass().getResource(String.format("/openapi/%s", specFileName))).toURI());
        final Path targetPath = Paths.get(getTargetDir(), "openapi-gen");

        return new OpenApiClientGeneratorWrapper(openApiSpec, targetPath);
    }

    private String getTargetDir() throws URISyntaxException {
        return Paths.get(requireNonNull(getClass().getResource("/")).toURI()).getParent().toString();
    }
}
