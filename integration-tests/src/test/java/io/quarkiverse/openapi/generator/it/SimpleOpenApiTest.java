package io.quarkiverse.openapi.generator.it;

import static io.quarkiverse.openapi.generator.it.assertions.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SimpleOpenApiTest {

    @Test
    void circuitBreaker() throws IOException {
        Path generatedRestClient = Paths.get("target", "generated-sources", "open-api-json", "org", "acme",
                "openapi", "simple", "api", "DefaultApi.java");

        assertThat(generatedRestClient)
                .exists()
                .isRegularFile()
                .content().isNotEmpty();

        CompilationUnit compilationUnit = StaticJavaParser.parse(generatedRestClient);
        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();

        Optional<MethodDeclaration> byeMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals("byeGet"))
                .findAny();

        assertThat(byeMethod).isNotEmpty();

        assertThat(byeMethod.orElseThrow()).hasCircuitBreakerAnnotation()
                .hasFailOnAsString("{ java.lang.IllegalArgumentException.class, java.lang.NullPointerException.class }")
                .hasSkipOnAsString("{ java.lang.NumberFormatException.class, java.lang.IndexOutOfBoundsException.class }")
                .hasDelayAsString("33")
                .hasDelayUnitAsString("java.time.temporal.ChronoUnit.MILLIS")
                .hasRequestVolumeThresholdAsString("42")
                .hasFailureRatioAsString("3.14")
                .hasSuccessThresholdAsString("22");

        methodDeclarations.stream()
                .filter(method -> !method.getNameAsString().equals("byeGet"))
                .forEach(method -> assertThat(method).doesNotHaveCircuitBreakerAnnotation());
    }
}
