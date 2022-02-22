package io.quarkiverse.openapi.generator.deployment.wrapper;

import static io.quarkiverse.openapi.generator.deployment.assertions.Assertions.assertThat;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.EnumConstantDeclaration;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import io.quarkiverse.openapi.generator.deployment.circuitbreaker.CircuitBreakerConfiguration;

public class OpenApiClientGeneratorWrapperTest {

    @Test
    void verifyCommonGenerated() throws URISyntaxException {
        final Path petstoreOpenApi = Path
                .of(requireNonNull(this.getClass().getResource("/openapi/petstore-openapi.json")).toURI());
        final Path targetPath = Paths.get(getTargetDir(), "openapi-gen");
        final OpenApiClientGeneratorWrapper generatorWrapper = new OpenApiClientGeneratorWrapper(petstoreOpenApi, targetPath);
        final List<File> generatedFiles = generatorWrapper.generate();
        assertNotNull(generatedFiles);
        assertFalse(generatedFiles.isEmpty());
    }

    @Test
    void verifyAuthBasicGenerated() throws URISyntaxException {
        final Path petstoreOpenApi = Path
                .of(requireNonNull(this.getClass().getResource("/openapi/petstore-openapi-httpbasic.json")).toURI());
        final Path targetPath = Paths.get(getTargetDir(), "openapi-gen");
        final OpenApiClientGeneratorWrapper generatorWrapper = new OpenApiClientGeneratorWrapper(petstoreOpenApi, targetPath);
        final List<File> generatedFiles = generatorWrapper.generate();
        assertTrue(generatedFiles.stream().anyMatch(f -> f.getName().equals("CompositeAuthenticationProvider.java")));
    }

    @Test
    void verifyAuthBearerGenerated() throws URISyntaxException {
        final Path petstoreOpenApi = Path
                .of(requireNonNull(this.getClass().getResource("/openapi/petstore-openapi-bearer.json")).toURI());
        final Path targetPath = Paths.get(getTargetDir(), "openapi-gen");
        final OpenApiClientGeneratorWrapper generatorWrapper = new OpenApiClientGeneratorWrapper(petstoreOpenApi, targetPath);
        final List<File> generatedFiles = generatorWrapper.generate();
        assertTrue(generatedFiles.stream().anyMatch(f -> f.getName().equals("CompositeAuthenticationProvider.java")));
    }

    @Test
    void verifyEnumGeneration() throws URISyntaxException, FileNotFoundException {
        final Path issue28Path = Path
                .of(requireNonNull(this.getClass().getResource("/openapi/issue-28.yaml")).toURI());
        final Path targetPath = Paths.get(getTargetDir(), "openapi-gen");
        final OpenApiClientGeneratorWrapper generatorWrapper = new OpenApiClientGeneratorWrapper(issue28Path, targetPath)
                .withBasePackage("org.issue28");

        final List<File> generatedFiles = generatorWrapper.generate();
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

    private String getTargetDir() throws URISyntaxException {
        return Paths.get(requireNonNull(getClass().getResource("/")).toURI()).getParent().toString();
    }

    @Test
    void circuitBreaker() throws URISyntaxException, FileNotFoundException {
        List<File> restClientFiles = generateRestClientFiles();

        assertNotNull(restClientFiles);
        assertFalse(restClientFiles.isEmpty());

        Optional<File> file = restClientFiles.stream()
                .filter(f -> f.getName().endsWith("DefaultApi.java"))
                .findAny();

        assertThat(file).isNotEmpty();

        CompilationUnit compilationUnit = StaticJavaParser.parse(file.orElseThrow());
        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();

        Optional<MethodDeclaration> byeMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals("byeGet"))
                .findAny();

        assertThat(byeMethod).isNotEmpty();

        assertThat(byeMethod.orElseThrow())
                .hasCircuitBreakerAnnotation()
                .hasDelayAsString("33")
                .hasSuccessThresholdAsString("5")
                .doesNotHaveDelayUnit()
                .doesNotHaveFailOn()
                .doesNotHaveFailureRatio()
                .doesNotHaveRequestVolumeThreshold()
                .doesNotHaveSkipOn();

        methodDeclarations.stream()
                .filter(m -> !m.getNameAsString().equals("byeGet"))
                .forEach(m -> assertThat(m).doesNotHaveCircuitBreakerAnnotation());
    }

    private List<File> generateRestClientFiles() throws URISyntaxException {
        Path targetPath = Paths.get(getTargetDir(), "openapi-gen");

        List<CircuitBreakerConfiguration.Operation> operations = List.of(
                new CircuitBreakerConfiguration.Operation("opThatDoesNotExist", Map.of()),
                new CircuitBreakerConfiguration.Operation("byeGet", Map.of("delay", "33", "successThreshold", "5")));

        CircuitBreakerConfiguration circuitBreakerConfiguration = CircuitBreakerConfiguration.builder()
                .enabled(true)
                .operations(operations)
                .build();

        Path simpleOpenApiFile = Path.of(Objects.requireNonNull(getClass().getResource("/openapi/simple-openapi.json"))
                .toURI());

        OpenApiClientGeneratorWrapper generatorWrapper = new OpenApiClientGeneratorWrapper(simpleOpenApiFile, targetPath)
                .withCircuitBreakerConfiguration(circuitBreakerConfiguration);

        return generatorWrapper.generate();
    }
}
