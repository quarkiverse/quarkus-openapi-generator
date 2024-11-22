package io.quarkiverse.openapi.generator.deployment.wrapper;

import static io.quarkiverse.openapi.generator.deployment.assertions.Assertions.assertThat;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.type.Type;

import io.quarkiverse.openapi.generator.annotations.GeneratedClass;
import io.quarkiverse.openapi.generator.annotations.GeneratedMethod;
import io.quarkiverse.openapi.generator.deployment.MockConfigUtils;
import io.quarkiverse.openapi.generator.deployment.codegen.ClassCodegenConfigParser;

public class OpenApiClientGeneratorWrapperTest {

    private static Optional<MethodDeclaration> getMethodDeclarationByIdentifier(List<MethodDeclaration> methodDeclarations,
            String methodName) {
        return methodDeclarations.stream().filter(md -> md.getName().getIdentifier().equals(methodName)).findAny();
    }

    @Test
    void verifyDiscriminatorGeneration() throws java.net.URISyntaxException, FileNotFoundException {
        OpenApiClientGeneratorWrapper generatorWrapper = createGeneratorWrapper("issue-852.json");
        final List<File> generatedFiles = generatorWrapper.generate("org.issue852");

        assertNotNull(generatedFiles);
        assertFalse(generatedFiles.isEmpty());

        final Optional<File> classWithDiscriminator = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("PostRevisionForDocumentRequest.java")).findFirst();
        assertThat(classWithDiscriminator).isPresent();

        final CompilationUnit compilationUnit = StaticJavaParser.parse(classWithDiscriminator.orElseThrow());
        assertThat(compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .flatMap(first -> first.getAnnotationByClass(com.fasterxml.jackson.annotation.JsonSubTypes.class)))
                .isPresent();
    }

    @Test
    void verifyFlink() throws URISyntaxException, FileNotFoundException {
        OpenApiClientGeneratorWrapper generatorWrapper = createGeneratorWrapper("issue-flink.yaml");
        final List<File> generatedFiles = generatorWrapper.generate("org.acme.flink");
        assertNotNull(generatedFiles);
        assertFalse(generatedFiles.isEmpty());

        final Optional<File> duplicatedVars = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("AsynchronousOperationResultOperation.java")).findFirst();
        assertThat(duplicatedVars).isPresent();

        CompilationUnit compilationUnit = StaticJavaParser.parse(duplicatedVars.orElseThrow());
        List<VariableDeclarator> vars = compilationUnit.findAll(VariableDeclarator.class);
        assertThat(vars).isNotEmpty();

        // This openApi file has a duplicated field
        assertThat(vars.stream()
                .filter(v -> "failureCause".equals(v.getNameAsString())).count()).isEqualTo(4);
    }

    @Test
    void verifySuffixPrefix() throws URISyntaxException {
        OpenApiClientGeneratorWrapper generatorWrapper = createGeneratorWrapper("suffix-prefix-openapi.json");
        String CUSTOM_API_SUFFIX = "CustomAPISuffix";
        String CUSTOM_MODEL_SUFFIX = "CustomModelSuffix";
        String CUSTOM_MODEL_PREFIX = "CustomModelPrefix";

        generatorWrapper.withApiNameSuffix(CUSTOM_API_SUFFIX);
        generatorWrapper.withModelNameSuffix(CUSTOM_MODEL_SUFFIX);
        generatorWrapper.withModelNamePrefix(CUSTOM_MODEL_PREFIX);
        final List<File> generatedFiles = generatorWrapper.generate("org.petstore.suffixprefix");
        assertNotNull(generatedFiles);
        assertFalse(generatedFiles.isEmpty());
        for (File f : generatedFiles) {
            String name = f.getName();
            String path = f.getPath();
            if (path.contains("/api/")) {
                assertTrue(name.endsWith(String.format("%s.java", CUSTOM_API_SUFFIX)));
            } else if (path.contains("/model/")) {
                assertTrue(name.startsWith(CUSTOM_MODEL_PREFIX));
                assertTrue(name.endsWith(String.format("%s.java", CUSTOM_MODEL_SUFFIX)));
            }
        }
    }

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
                .filter(m -> m.getNameAsString().equals("filter"))
                .findAny();
        assertThat(initMethod).isPresent();

        String fileContent = compilationUnit.toString();
        assertTrue(fileContent.contains(
                "@io.quarkiverse.openapi.generator.markers.BasicAuthenticationMarker(name = \"basic\", openApiSpecId = \"petstore_openapi_httpbasic_json\")"));
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

        assertThat(fields).extracting(FieldDeclaration::getVariables).hasSize(10);

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
        assertThat(imports).contains("java.io.InputStream")
                .doesNotContain("java.io.File");
    }

    @Test
    void withoutAnyTypeOrImportMappingsItShouldGenerateUsingJava8DatesAndTimes()
            throws URISyntaxException, FileNotFoundException {
        List<File> generatedFiles = createGeneratorWrapper("datetime-regression.yml")
                .generate("org.datetime.regression");

        Optional<File> file = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("SomeName.java"))
                .findAny();
        assertThat(file).isNotEmpty();
        CompilationUnit compilationUnit = StaticJavaParser.parse(file.orElseThrow());
        List<ClassOrInterfaceDeclaration> classes = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        assertThat(classes).hasSize(2);
        ClassOrInterfaceDeclaration generatedPojoClass = classes.get(0);

        verifyGeneratedDateAndTimeTypes(
                generatedPojoClass,
                Map.of(
                        "someDate", "LocalDate",
                        "someDateTime", "OffsetDateTime",
                        "dateArray", "List<LocalDate>",
                        "dateTimeArray", "List<OffsetDateTime>",
                        "dateSet", "Set<LocalDate>",
                        "dateTimeSet", "Set<OffsetDateTime>",
                        "dateMap", "Map<String,LocalDate>",
                        "dateTimeMap", "Map<String,OffsetDateTime>"));
        assertThat(compilationUnit.getImports().stream().map(NodeWithName::getNameAsString))
                .contains("java.time.LocalDate", "java.time.OffsetDateTime");
    }

    @Test
    void shouldBeAbleToEnableMutiny() throws URISyntaxException, FileNotFoundException {
        List<File> generatedFiles = createGeneratorWrapper("simple-openapi.json")
                .withMutiny(true)
                .generate("org.mutiny.enabled");

        Optional<File> file = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("DefaultApi.java"))
                .findAny();
        assertThat(file).isNotEmpty();
        CompilationUnit compilationUnit = StaticJavaParser.parse(file.orElseThrow());
        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();

        methodDeclarations.forEach(m -> {
            var returnType = m.getType().toString();
            assertTrue(returnType.startsWith("io.smallrye.mutiny.Uni<"));
            assertTrue(returnType.endsWith(">"));
        });

    }

    @Test
    void shouldBeAbleToApplyMutinyOnSpecificEndpoints() throws URISyntaxException, FileNotFoundException {
        List<File> generatedFiles = createGeneratorWrapper("simple-openapi.json")
                .withMutiny(true)
                .withMutinyReturnTypes(Map.of("helloMethod", "Uni", "Bye method_get", "Multi"))
                .generate("org.mutiny.enabled");

        Optional<File> file = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("DefaultApi.java"))
                .findAny();
        assertThat(file).isNotEmpty();
        CompilationUnit compilationUnit = StaticJavaParser.parse(file.orElseThrow());
        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();

        Optional<MethodDeclaration> helloMethodDeclaration = getMethodDeclarationByIdentifier(methodDeclarations,
                "helloMethod");
        Optional<MethodDeclaration> byeMethodGetDeclaration = getMethodDeclarationByIdentifier(methodDeclarations,
                "byeMethodGet");
        Optional<MethodDeclaration> getUserDeclaration = getMethodDeclarationByIdentifier(methodDeclarations,
                "getUser");
        Optional<MethodDeclaration> getNumbersDeclaration = getMethodDeclarationByIdentifier(methodDeclarations,
                "getNumbers");

        assertThat(helloMethodDeclaration).hasValueSatisfying(
                methodDeclaration -> assertEquals("io.smallrye.mutiny.Uni<String>", methodDeclaration.getType().toString()));
        assertThat(byeMethodGetDeclaration).hasValueSatisfying(
                methodDeclaration -> assertEquals("io.smallrye.mutiny.Multi<String>", methodDeclaration.getType().toString()));
        assertThat(getUserDeclaration).hasValueSatisfying(
                methodDeclaration -> assertEquals("GetUser200Response", methodDeclaration.getType().toString()));
        assertThat(getNumbersDeclaration).hasValueSatisfying(
                methodDeclaration -> assertEquals("List<Integer>", methodDeclaration.getType().toString()));
    }

    @Test
    void shouldBeAbleToApplyMutinyOnSpecificEndpointsWhenUserDefineWrongConfiguration()
            throws URISyntaxException, FileNotFoundException {
        List<File> generatedFiles = createGeneratorWrapper("simple-openapi.json")
                .withMutiny(true)
                .withMutinyReturnTypes(Map.of("helloMethod", "Uni", "Bye method_get", "BadConfig"))
                .generate("org.mutiny.enabled");

        Optional<File> file = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("DefaultApi.java"))
                .findAny();
        assertThat(file).isNotEmpty();
        CompilationUnit compilationUnit = StaticJavaParser.parse(file.orElseThrow());
        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();

        Optional<MethodDeclaration> helloMethodDeclaration = getMethodDeclarationByIdentifier(methodDeclarations,
                "helloMethod");
        Optional<MethodDeclaration> byeMethodGetDeclaration = getMethodDeclarationByIdentifier(methodDeclarations,
                "byeMethodGet");
        Optional<MethodDeclaration> getUserDeclaration = getMethodDeclarationByIdentifier(methodDeclarations,
                "getUser");
        Optional<MethodDeclaration> getNumbersDeclaration = getMethodDeclarationByIdentifier(methodDeclarations,
                "getNumbers");

        assertThat(helloMethodDeclaration).hasValueSatisfying(
                methodDeclaration -> assertEquals("io.smallrye.mutiny.Uni<String>", methodDeclaration.getType().toString()));
        assertThat(byeMethodGetDeclaration).hasValueSatisfying(
                methodDeclaration -> assertEquals("io.smallrye.mutiny.Uni<String>", methodDeclaration.getType().toString()));
        assertThat(getUserDeclaration).hasValueSatisfying(
                methodDeclaration -> assertEquals("GetUser200Response", methodDeclaration.getType().toString()));
        assertThat(getNumbersDeclaration).hasValueSatisfying(
                methodDeclaration -> assertEquals("List<Integer>", methodDeclaration.getType().toString()));
    }

    @Test
    void shouldBeAbleToAddCustomDateAndTimeMappings() throws URISyntaxException, FileNotFoundException {
        List<File> generatedFiles = createGeneratorWrapper("datetime-regression.yml")
                .withTypeMappings(Map.of(
                        "date", "ThaiBuddhistDate",
                        "DateTime", "Instant"))
                .withImportMappings(Map.of(
                        "ThaiBuddhistDate", "java.time.chrono.ThaiBuddhistDate",
                        "Instant", "java.time.Instant"))
                .generate("org.datetime.mappings");
        Optional<File> file = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("SomeName.java"))
                .findAny();
        assertThat(file).isNotEmpty();
        CompilationUnit compilationUnit = StaticJavaParser.parse(file.orElseThrow());
        List<ClassOrInterfaceDeclaration> classes = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        assertThat(classes).hasSize(2);
        ClassOrInterfaceDeclaration generatedPojoClass = classes.get(0);

        verifyGeneratedDateAndTimeTypes(
                generatedPojoClass,
                Map.of(
                        "someDate", "ThaiBuddhistDate",
                        "someDateTime", "Instant",
                        "dateArray", "List<ThaiBuddhistDate>",
                        "dateTimeArray", "List<Instant>",
                        "dateSet", "Set<ThaiBuddhistDate>",
                        "dateTimeSet", "Set<Instant>",
                        "dateMap", "Map<String,ThaiBuddhistDate>",
                        "dateTimeMap", "Map<String,Instant>"));
        assertThat(compilationUnit.getImports().stream().map(NodeWithName::getNameAsString))
                .contains("java.time.chrono.ThaiBuddhistDate", "java.time.Instant");
    }

    private void verifyGeneratedDateAndTimeTypes(
            ClassOrInterfaceDeclaration classDeclaration,
            Map<String, String> expectedFieldsAndTypes) {
        expectedFieldsAndTypes.forEach((fieldName, expectedFieldType) -> {
            Optional<FieldDeclaration> fieldDeclaration = classDeclaration.getFieldByName(fieldName);
            assertThat(fieldDeclaration).isPresent();

            Optional<Node> fieldVariableDeclaration = fieldDeclaration.orElseThrow().getChildNodes().stream()
                    .filter(it -> it instanceof VariableDeclarator)
                    .findFirst();
            assertThat(fieldVariableDeclaration).isPresent();

            Type fieldType = ((VariableDeclarator) fieldVariableDeclaration.orElseThrow()).getType();
            assertThat(fieldType.asString()).isEqualTo(expectedFieldType);
        });
    }

    @Test
    void verifyAdditionalModelTypeAnnotations() throws URISyntaxException {
        List<File> generatedFiles = createGeneratorWrapper("petstore-openapi.json")
                .withAdditionalModelTypeAnnotationsConfig("@org.test.Foo;@org.test.Bar")
                .generate("org.additionalmodeltypeannotations");
        assertFalse(generatedFiles.isEmpty());

        generatedFiles.stream()
                .filter(file -> file.getPath().matches(".*/model/.*.java"))
                .forEach(this::verifyModelAdditionalAnnotations);
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
    void verifyAdditionalApiTypeAnnotations() throws URISyntaxException {
        List<File> generatedFiles = createGeneratorWrapper("petstore-openapi.json")
                .withEnabledSecurityGeneration(false)
                .withAdditionalApiTypeAnnotationsConfig("@org.test.Foo;@org.test.Bar")
                .generate("org.additionalapitypeannotations");
        assertFalse(generatedFiles.isEmpty());

        generatedFiles.stream()
                .filter(file -> file.getPath().matches(".*api.*Api.java"))
                .forEach(this::verifyApiAdditionalAnnotations);
    }

    private void verifyApiAdditionalAnnotations(File file) {
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
    void verifyAdditionalRequestArgs() throws URISyntaxException {
        List<File> generatedFiles = createGeneratorWrapper("petstore-openapi.json")
                .withEnabledSecurityGeneration(false)
                .withAdditionalRequestArgs(
                        "@HeaderParam(\"jaxrs-style-header\") String headerValue;@HeaderParam(\"x-correlation-id\") String correlationId;@PathParam(\"stream\") String stream")
                .generate("org.additionalHTTPHeaders");
        assertFalse(generatedFiles.isEmpty());

        generatedFiles.stream()
                .filter(file -> file.getPath()
                        .matches(".*api.*Api.java"))
                .forEach(this::verifyApiAdditionalHTTPHeaders);
    }

    private void verifyApiAdditionalHTTPHeaders(File file) {
        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);
            compilationUnit.findAll(MethodDeclaration.class)
                    .forEach(c -> {
                        assertParameter(c.getParameterByName("correlationId"),
                                "String",
                                Map.of("HeaderParam",
                                        "\"x-correlation-id\""));
                        assertParameter(c.getParameterByName("headerValue"),
                                "String",
                                Map.of("HeaderParam",
                                        "\"jaxrs-style-header\""));
                        assertParameter(c.getParameterByName("stream"),
                                "String",
                                Map.of("PathParam",
                                        "\"stream\""));
                    });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void assertParameter(Optional<Parameter> optionalParameter,
            String type,
            Map<String, String> annotations) {
        assertThat(optionalParameter).isPresent();
        var parameter = optionalParameter.orElseThrow();
        assertThat(parameter.getTypeAsString()).isEqualTo(type);
        annotations.forEach((annotationName, annotationValue) -> {
            var parameterAnnotation = parameter.getAnnotationByName(annotationName);
            assertThat(parameterAnnotation).isPresent();
            assertThat(parameterAnnotation.get()
                    .asSingleMemberAnnotationExpr()
                    .getMemberValue()
                    .toString()).hasToString(annotationValue);
        });
    }

    @Test
    void verifyCookieParams() throws URISyntaxException {
        List<File> generatedFiles = createGeneratorWrapper("petstore-openapi.json")
                .generate("org.cookieParams");

        generatedFiles.stream()
                .filter(file -> file.getPath()
                        .matches("PetApi.java"))
                .forEach(file -> {
                    try {
                        CompilationUnit compilationUnit = StaticJavaParser.parse(file);
                        var positiveFounds = compilationUnit.findAll(MethodDeclaration.class)
                                .stream()
                                .filter(c -> c.getNameAsString()
                                        .equals("findPetsByStatus"))
                                .filter(c -> {
                                    assertParameter(c.getParameterByName("exampleCookie"),
                                            "String",
                                            Map.of("CookieParam",
                                                    "\"example-cookie\""));
                                    return true;
                                })
                                .count();
                        assertThat(positiveFounds).isEqualTo(1);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
    }

    @Test
    void verifyAPINormalization() throws Exception {
        final List<File> generatedFiles = this.createGeneratorWrapper("open-api-normalizer.json")
                .withOpenApiNormalizer(Map.of("REF_AS_PARENT_IN_ALLOF", "true", "REFACTOR_ALLOF_WITH_PROPERTIES_ONLY", "true"))
                .generate("org.acme.openapi.animals");

        assertNotNull(generatedFiles);
        assertFalse(generatedFiles.isEmpty());

        Optional<File> file = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("Primate.java"))
                .findAny();
        assertThat(file).isNotEmpty();
        CompilationUnit compilationUnit = StaticJavaParser.parse(file.orElseThrow());
        List<ClassOrInterfaceDeclaration> types = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);

        assertThat(types).hasSize(2);
        assertThat(types.get(0).getExtendedTypes()).hasSize(1);
        assertThat(types.get(0).getExtendedTypes(0).getName()).isEqualTo(new SimpleName("Mammal"));
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

        return new OpenApiClassicClientGeneratorWrapper(openApiSpec, targetPath, false, true);
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
