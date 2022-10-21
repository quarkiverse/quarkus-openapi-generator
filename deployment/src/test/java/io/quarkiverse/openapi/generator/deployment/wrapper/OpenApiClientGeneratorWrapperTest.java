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
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.codegen.config.GlobalSettings;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;

import io.quarkiverse.openapi.generator.annotations.GeneratedClass;
import io.quarkiverse.openapi.generator.annotations.GeneratedMethod;
import io.quarkiverse.openapi.generator.deployment.MockConfigUtils;
import io.quarkiverse.openapi.generator.deployment.codegen.ClassCodegenConfigParser;

public class OpenApiClientGeneratorWrapperTest {

    @Test
    void verifyCommonGenerated() throws URISyntaxException {
        final List<File> generatedFiles = createGeneratorWrapper("petstore-openapi.json").generate("org.petstore");
        assertNotNull(generatedFiles);
        assertFalse(generatedFiles.isEmpty());
    }

    @Test
    void verifyAuthBasicGenerated() throws URISyntaxException {
        final List<File> generatedFiles = createGeneratorWrapper("petstore-openapi-httpbasic.json").generate("org.petstore");
        assertTrue(generatedFiles.stream().anyMatch(f -> f.getName().equals("CompositeAuthenticationProvider.java")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "basic", "undefined" })
    void verifyAuthBasicWithMissingSecurityDefinition(String defaultSecurityScheme)
            throws URISyntaxException, FileNotFoundException {
        GlobalSettings.setProperty(OpenApiClientGeneratorWrapper.DEFAULT_SECURITY_SCHEME, defaultSecurityScheme);
        final List<File> generatedFiles = createGeneratorWrapper("petstore-openapi-httpbasic.json").generate("org.petstore");
        final Optional<File> authProviderFile = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("CompositeAuthenticationProvider.java")).findFirst();
        assertThat(authProviderFile).isPresent();

        CompilationUnit compilationUnit = StaticJavaParser.parse(authProviderFile.orElseThrow());
        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();

        Optional<MethodDeclaration> initMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals("init"))
                .findAny();
        assertThat(initMethod).isPresent();

        String fileContent = compilationUnit.toString();
        assertTrue(fileContent.contains("addAuthenticationProvider"));
        if (!defaultSecurityScheme.equals("undefined")) {
            assertTrue(fileContent.contains("addOperation"));
        }
    }

    @Test
    void verifyAuthBearerGenerated() throws URISyntaxException {
        final List<File> generatedFiles = createGeneratorWrapper("petstore-openapi-bearer.json").generate("org.petstore");
        assertTrue(generatedFiles.stream().anyMatch(f -> f.getName().equals("CompositeAuthenticationProvider.java")));
    }

    @Test
    void verifyEnumGeneration() throws URISyntaxException, FileNotFoundException {
        final List<File> generatedFiles = createGeneratorWrapper("issue-28.yaml")
                .generate("org.issue28");
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
        final Map<String, Object> codegenConfig = ClassCodegenConfigParser
                .parse(MockConfigUtils.getTestConfig("/codegen/application.properties"), "org.issue38");
        final List<File> generatedFiles = this.createGeneratorWrapper("issue-38.yaml")
                .withClassesCodeGenConfig(codegenConfig)
                .generate("org.issue38");

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

        // this class has a deprecated attribute, so we check the default behavior
        final Optional<File> connectorDeploymentStatus = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("ConnectorDeploymentStatus.java")).findFirst();
        assertThat(connectorDeploymentStatus).isPresent();
        final CompilationUnit cu3 = StaticJavaParser.parse(connectorDeploymentStatus.orElseThrow());
        final List<FieldDeclaration> fields3 = cu3.findAll(FieldDeclaration.class);

        assertThat(fields3.stream()
                .flatMap(v -> v.getVariables().stream())
                .anyMatch(f -> f.getNameAsString().equals("availableUpgrades")))
                .isTrue();
    }

    @Test
    void verifyDeprecatedOperations() throws URISyntaxException, FileNotFoundException {
        final Map<String, Object> codegenConfig = ClassCodegenConfigParser
                .parse(MockConfigUtils.getTestConfig("/deprecated/application.properties"), "org.deprecated");
        List<File> generatedFiles = this.createGeneratorWrapper("deprecated.json")
                .withClassesCodeGenConfig(codegenConfig)
                .generate("org.deprecated");
        assertFalse(generatedFiles.isEmpty());

        Optional<File> file = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("DefaultApi.java"))
                .findAny();

        assertThat(file).isNotEmpty();

        CompilationUnit compilationUnit = StaticJavaParser.parse(file.orElseThrow());

        compilationUnit.findAll(ClassOrInterfaceDeclaration.class)
                .forEach(c -> assertThat(c.getAnnotationByClass(GeneratedClass.class)).isPresent());

        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();

        methodDeclarations.forEach(m -> assertThat(m.getAnnotationByClass(GeneratedMethod.class)).isPresent());

        // hello operation is NOT deprecated and should be generated
        Optional<MethodDeclaration> helloMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals("helloGet"))
                .findAny();

        assertThat(helloMethod).isNotEmpty();

        // bye operation is deprecated and should NOT be generated
        Optional<MethodDeclaration> byeMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals("byeGet"))
                .findAny();

        assertThat(byeMethod).isEmpty();
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

        String byeMethodGet = "byeMethodGet";
        String helloMethod = "helloMethod";
        methodDeclarations.forEach(m -> {
            Optional<AnnotationExpr> annotation = m.getAnnotationByClass(GeneratedMethod.class);
            assertThat(annotation).isPresent();
            if (byeMethodGet.equals(m.getNameAsString())) {
                assertThat(annotation.get().asSingleMemberAnnotationExpr().getMemberValue().asStringLiteralExpr().getValue())
                        .isEqualTo("Bye method_get");
            } else if (helloMethod.equals(m.getNameAsString())) {
                assertThat(annotation.get().asSingleMemberAnnotationExpr().getMemberValue().asStringLiteralExpr().getValue())
                        .isEqualTo("helloMethod");
            }
        });

        Optional<MethodDeclaration> byeMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals(byeMethodGet))
                .findAny();

        assertThat(byeMethod).isNotEmpty();

        assertThat(byeMethod.orElseThrow())
                .hasCircuitBreakerAnnotation()
                .doesNotHaveAnyCircuitBreakerAttribute();

        methodDeclarations.stream()
                .filter(m -> !m.getNameAsString().equals(byeMethodGet))
                .forEach(m -> assertThat(m).doesNotHaveCircuitBreakerAnnotation());
    }

    @Test
    void verifyMultipartFormAnnotationIsGeneratedForParameter() throws URISyntaxException, FileNotFoundException {
        List<File> generatedFiles = createGeneratorWrapper("multipart-openapi.yml")
                .withSkipFormModelConfig("false")
                .generate("org.acme");
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
                .generate("org.acme");
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

        List<FieldDeclaration> fields = multipartPojo.orElseThrow().getFields();
        assertThat(fields).hasSize(3);
        fields.forEach(field -> {
            assertThat(field.getAnnotationByName("FormParam")).isPresent();
            assertThat(field.getAnnotationByName("PartType")).isPresent();
        });

        Optional<VariableDeclarator> fileUploadVariable = findVariableByName(fields, "profileImage");
        assertThat(fileUploadVariable.orElseThrow().getType().asString()).isEqualTo("File");
    }

    @Test
    void shouldMapFileTypeToFullyQualifiedInputStream() throws URISyntaxException, FileNotFoundException {
        List<File> generatedFiles = createGeneratorWrapper("multipart-openapi.yml")
                .withTypeMappings(Map.of("File", "java.io.InputStream"))
                .generate("org.acme");
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

        Optional<VariableDeclarator> fileUploadVariable = findVariableByName(multipartPojo.orElseThrow().getFields(),
                "profileImage");
        assertThat(fileUploadVariable.orElseThrow().getType().asString()).isEqualTo("java.io.InputStream");
    }

    @Test
    void shouldReplaceFileImportWithInputStream() throws URISyntaxException, FileNotFoundException {
        List<File> generatedFiles = createGeneratorWrapper("multipart-openapi.yml")
                .withSkipFormModelConfig("false")
                .withTypeMappings(Map.of("File", "InputStream"))
                .withImportMappings(Map.of("File", "java.io.InputStream"))
                .generate("org.acme");
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

        Optional<VariableDeclarator> fileUploadVariable = findVariableByName(multipartPojo.orElseThrow().getFields(),
                "profileImage");
        assertThat(fileUploadVariable.orElseThrow().getType().asString()).isEqualTo("InputStream");

        List<String> imports = compilationUnit.findAll(ImportDeclaration.class)
                .stream()
                .map(importDeclaration -> importDeclaration.getName().asString())
                .collect(Collectors.toList());
        assertThat(imports).contains("java.io.InputStream");
        assertThat(imports).doesNotContain("java.io.File");
    }

    @Test
    void verifyAdditionalModelTypeAnnotations() throws URISyntaxException {
        List<File> generatedFiles = createGeneratorWrapper("petstore-openapi.json")
                .withAdditionalModelTypeAnnotationsConfig("@org.test.Foo;@org.test.Bar")
                .generate("org.additionalmodeltypeannotations");
        assertFalse(generatedFiles.isEmpty());

        generatedFiles.stream()
                .filter(file -> file.getPath().matches(".*/model/.*.java"))
                .forEach(file -> verifyModelAdditionalAnnotations(file));
    }

    private void verifyModelAdditionalAnnotations(File file) {
        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);
            compilationUnit.findAll(ClassOrInterfaceDeclaration.class)
                    .forEach(c -> {
                        assertThat(c.getAnnotationByName("Foo")).isPresent();
                        assertThat(c.getAnnotationByName("Bar")).isPresent();
                    });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void verifyCustomRegisterProviderAnnotations() throws URISyntaxException {
        List<File> generatedFiles = createGeneratorWrapper("petstore-openapi.json")
                .withCustomRegisterProviders("org.test.Foo,org.test.Bar")
                .generate("org.test");
        assertFalse(generatedFiles.isEmpty());

        List<File> filteredGeneratedFiles = generatedFiles.stream()
                .filter(file -> file.getPath().matches(".*api.*Api.java")).collect(Collectors.toList());

        assertThat(filteredGeneratedFiles).isNotEmpty();

        filteredGeneratedFiles.forEach(file -> {
            try {
                CompilationUnit compilationUnit = StaticJavaParser.parse(file);
                compilationUnit.findAll(ClassOrInterfaceDeclaration.class)
                        .forEach(c -> {
                            assertThat(getRegisterProviderAnnotation(c, "org.test.Foo.class")).isPresent();
                            assertThat(getRegisterProviderAnnotation(c, "org.test.Bar.class")).isPresent();
                        });
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    @Test
    void verifyCustomRegisterProviderAnnotationsWithoutSecurity() throws URISyntaxException {
        List<File> generatedFiles = createGeneratorWrapper("petstore-openapi-custom-register-provider.json")
                .withCustomRegisterProviders("org.test.Foo,org.test.Bar")
                .generate("org.test");
        assertFalse(generatedFiles.isEmpty());

        List<File> filteredGeneratedFiles = generatedFiles.stream()
                .filter(file -> file.getPath().matches(".*api.*Api.java")).collect(Collectors.toList());

        assertThat(filteredGeneratedFiles).isNotEmpty();

        filteredGeneratedFiles.forEach(file -> {
            try {
                CompilationUnit compilationUnit = StaticJavaParser.parse(file);
                compilationUnit.findAll(ClassOrInterfaceDeclaration.class)
                        .forEach(c -> {
                            assertThat(getRegisterProviderAnnotation(c, "org.test.Foo.class")).isPresent();
                            assertThat(getRegisterProviderAnnotation(c, "org.test.Bar.class")).isPresent();
                        });

                List<String> imports = compilationUnit.findAll(ImportDeclaration.class)
                        .stream()
                        .map(importDeclaration -> importDeclaration.getName().asString())
                        .collect(Collectors.toList());
                assertThat(imports).contains("org.eclipse.microprofile.rest.client.annotation.RegisterProvider");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    private Optional<AnnotationExpr> getRegisterProviderAnnotation(ClassOrInterfaceDeclaration declaration,
            String annotationValue) {
        return declaration.getAnnotations()
                .stream()
                .filter(annotationExpr -> "RegisterProvider".equals(annotationExpr.getNameAsString()) &&
                        annotationExpr instanceof SingleMemberAnnotationExpr &&
                        annotationValue.equals(((SingleMemberAnnotationExpr) annotationExpr).getMemberValue()
                                .toString()))
                .findFirst();
    }

    private List<File> generateRestClientFiles() throws URISyntaxException {
        OpenApiClientGeneratorWrapper generatorWrapper = createGeneratorWrapper("simple-openapi.json")
                .withCircuitBreakerConfig(Map.of(
                        "org.openapitools.client.api.DefaultApi", List.of("opThatDoesNotExist", "byeMethodGet")));

        return generatorWrapper.generate("org.openapitools.client");
    }

    private OpenApiClientGeneratorWrapper createGeneratorWrapper(String specFileName) throws URISyntaxException {
        final Path openApiSpec = Path
                .of(requireNonNull(this.getClass().getResource(String.format("/openapi/%s", specFileName))).toURI());
        final Path targetPath = Paths.get(getTargetDir(), "openapi-gen");

        return new OpenApiClientGeneratorWrapper(openApiSpec, targetPath, false, true);
    }

    private String getTargetDir() throws URISyntaxException {
        return Paths.get(requireNonNull(getClass().getResource("/")).toURI()).getParent().toString();
    }

    private Optional<VariableDeclarator> findVariableByName(List<FieldDeclaration> fields, String name) {
        return fields.stream().map(field -> field.getVariable(0))
                .filter((VariableDeclarator variable) -> name.equals(variable.getName().asString()))
                .findFirst();
    }
}
