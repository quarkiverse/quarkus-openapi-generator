package io.quarkiverse.openapi.generator.deployment.wrapper;

import static io.quarkiverse.openapi.generator.deployment.assertions.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import io.quarkiverse.openapi.generator.deployment.assertions.Assertions;

class QuarkusJavaClientCodegenTest {

    @ParameterizedTest
    @CsvSource({
            "/status/addressStatus,String,SLASH_STATUS_SLASH_ADDRESSSTATUS",
            "$,String,DOLLAR_SYMBOL",
            "/users,String,SLASH_USERS",
            "'  ',String,EMPTY",
            "123456,String,_123456",
            "quarkus_resources,String,QUARKUS_RESOURCES",
            "123456,Integer,NUMBER_123456", // old behavior
            "123+123,Long,NUMBER_123PLUS_123", // old behavior,
            "M123,String,M123",
            "MA456,String,MA456",
            "P1,String,P1",
    })
    void toEnumVarName(String value, String dataType, String expectedVarName) {

        QuarkusJavaClientCodegen quarkusJavaClientCodegen = new QuarkusJavaClientCodegen();

        String varName = quarkusJavaClientCodegen.toEnumVarName(value, dataType);

        Assertions.assertThat(varName).isEqualTo(expectedVarName);
    }

    @Test
    void unaccentString() {
        // Test basic accent removal
        assertThat(QuarkusJavaClientCodegen.unaccentString("Funcionário")).isEqualTo("Funcionario");
        assertThat(QuarkusJavaClientCodegen.unaccentString("Português")).isEqualTo("Portugues");
        assertThat(QuarkusJavaClientCodegen.unaccentString("Mão")).isEqualTo("Mao");
        assertThat(QuarkusJavaClientCodegen.unaccentString("Piso Sótão")).isEqualTo("Piso Sotao");

        // Test with no accents
        assertThat(QuarkusJavaClientCodegen.unaccentString("Hello World")).isEqualTo("Hello World");

        // Test with mixed characters
        assertThat(QuarkusJavaClientCodegen.unaccentString("Época ãé")).isEqualTo("Epoca ae");

        // Test with null
        assertThat(QuarkusJavaClientCodegen.unaccentString(null)).isNull();
    }

    @Test
    void verifyMultiSegmentParamsInOperationMarker() throws URISyntaxException, FileNotFoundException {
        OpenApiClientGeneratorWrapper generatorWrapper = createGeneratorWrapper("x-multi-segment-test.yaml");
        final List<File> generatedFiles = generatorWrapper.generate("org.multisegment");

        assertThat(generatedFiles).isNotEmpty();

        final Optional<File> apiFile = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("DefaultApi.java")).findFirst();
        assertThat(apiFile).isPresent();

        CompilationUnit compilationUnit = StaticJavaParser.parse(apiFile.orElseThrow());
        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();

        // Verify getRepoRef operation has multiSegmentParams={"ref"}
        Optional<MethodDeclaration> getRepoRefMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals("getRepoRef"))
                .findFirst();
        assertThat(getRepoRefMethod).isPresent();
        verifyOperationMarkerMultiSegmentParams(getRepoRefMethod.orElseThrow(), List.of("ref"));

        // Verify getDocFile operation has multiSegmentParams={"ref", "path"}
        Optional<MethodDeclaration> getDocFileMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals("getDocFile"))
                .findFirst();
        assertThat(getDocFileMethod).isPresent();
        verifyOperationMarkerMultiSegmentParams(getDocFileMethod.orElseThrow(), List.of("ref", "path"));

        // Verify getById operation has no multiSegmentParams attribute
        Optional<MethodDeclaration> getByIdMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals("getById"))
                .findFirst();
        assertThat(getByIdMethod).isPresent();
        verifyOperationMarkerNoMultiSegmentParams(getByIdMethod.orElseThrow());
    }

    private void verifyOperationMarkerMultiSegmentParams(MethodDeclaration method, List<String> expectedParams) {
        Optional<AnnotationExpr> operationMarker = method.getAnnotations().stream()
                .filter(a -> a.getNameAsString().contains("OperationMarker"))
                .findFirst();

        assertThat(operationMarker).isPresent();
        assertTrue(operationMarker.get().isNormalAnnotationExpr(),
                "OperationMarker should be a NormalAnnotationExpr when multiSegmentParams is present");

        NormalAnnotationExpr normalAnnotation = operationMarker.get().asNormalAnnotationExpr();
        Optional<MemberValuePair> multiSegmentParamsPair = normalAnnotation.getPairs().stream()
                .filter(pair -> pair.getNameAsString().equals("multiSegmentParams"))
                .findFirst();

        assertThat(multiSegmentParamsPair).isPresent();

        Expression value = multiSegmentParamsPair.get().getValue();
        assertTrue(value.isArrayInitializerExpr(), "multiSegmentParams should be an array");

        ArrayInitializerExpr arrayExpr = value.asArrayInitializerExpr();
        List<String> actualParams = arrayExpr.getValues().stream()
                .map(Expression::asStringLiteralExpr)
                .map(StringLiteralExpr::getValue)
                .toList();

        assertThat(actualParams).containsExactlyElementsOf(expectedParams);
    }

    private void verifyOperationMarkerNoMultiSegmentParams(MethodDeclaration method) {
        Optional<AnnotationExpr> operationMarker = method.getAnnotations().stream()
                .filter(a -> a.getNameAsString().contains("OperationMarker"))
                .findFirst();

        assertThat(operationMarker).isPresent();

        if (operationMarker.get().isNormalAnnotationExpr()) {
            NormalAnnotationExpr normalAnnotation = operationMarker.get().asNormalAnnotationExpr();
            Optional<MemberValuePair> multiSegmentParamsPair = normalAnnotation.getPairs().stream()
                    .filter(pair -> pair.getNameAsString().equals("multiSegmentParams"))
                    .findFirst();

            assertThat(multiSegmentParamsPair).isEmpty();
        }
        // If it's not a NormalAnnotationExpr, there's definitely no multiSegmentParams attribute
    }

    @Test
    void verifyInlineEnumReuse() throws URISyntaxException, FileNotFoundException {
        final List<File> generatedFiles = createGeneratorWrapper("inline-enum-reuse.json", true)
                .generate("org.enumreuse");
        assertFalse(generatedFiles.isEmpty());

        final Optional<File> orderFile = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("Order.java")).findFirst();
        assertThat(orderFile).isPresent();

        final Optional<File> shipmentFile = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("Shipment.java")).findFirst();
        assertThat(shipmentFile).isPresent();

        final Optional<File> sharedStatusFile = generatedFiles.stream()
                .filter(f -> f.getName().endsWith("SharedStatus.java")).findFirst();
        assertThat(sharedStatusFile).isPresent();

        final CompilationUnit orderCu = StaticJavaParser.parse(orderFile.orElseThrow());
        final CompilationUnit shipmentCu = StaticJavaParser.parse(shipmentFile.orElseThrow());

        assertThat(orderCu.toString()).contains("SharedStatus");
        assertThat(orderCu.toString()).doesNotContain("StatusEnum");

        assertThat(shipmentCu.toString()).contains("SharedStatus");
        assertThat(shipmentCu.toString()).doesNotContain("StatusEnum");

        final CompilationUnit statusCu = StaticJavaParser.parse(sharedStatusFile.orElseThrow());
        final List<EnumConstantDeclaration> constants = statusCu.findAll(EnumConstantDeclaration.class);
        assertThat(constants).hasSize(4)
                .extracting(EnumConstantDeclaration::getNameAsString)
                .containsExactlyInAnyOrder("CREATED", "PAID", "SHIPPED", "CANCELLED");
    }

    private OpenApiClientGeneratorWrapper createGeneratorWrapper(String specFileName) throws URISyntaxException {
        final Path openApiSpec = getOpenApiSpecPath(specFileName);
        OpenApiClientGeneratorWrapper wrapper = new OpenApiClassicClientGeneratorWrapper(openApiSpec,
                getOpenApiTargetPath(openApiSpec), false, true);
        return wrapper;
    }

    private OpenApiClientGeneratorWrapper createGeneratorWrapper(String specFileName, boolean reuseEnums)
            throws URISyntaxException {
        final Path openApiSpec = getOpenApiSpecPath(specFileName);
        OpenApiClientGeneratorWrapper wrapper = new OpenApiClassicClientGeneratorWrapper(openApiSpec,
                getOpenApiTargetPath(openApiSpec), false, true);
        return wrapper.withReuseEnums(reuseEnums);
    }

    private Path getOpenApiSpecPath(String specFileName) throws URISyntaxException {
        URL url = this.getClass().getResource("/openapi/" + specFileName);
        Objects.requireNonNull(url, "Could not find /openapi/" + specFileName);
        return Paths.get(url.toURI());
    }

    private Path getOpenApiTargetPath(Path openApiSpec) {
        URL url = Objects.requireNonNull(getClass().getResource("/"), "Could not locate classpath root");
        try {
            return Paths.get(url.toURI()).getParent().resolve("openapi-gen");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI for " + url, e);
        }
    }
}
