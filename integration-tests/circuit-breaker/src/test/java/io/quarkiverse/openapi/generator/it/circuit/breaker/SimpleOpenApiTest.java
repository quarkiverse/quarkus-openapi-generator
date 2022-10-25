package io.quarkiverse.openapi.generator.it.circuit.breaker;

import static io.quarkiverse.openapi.generator.it.circuit.breaker.assertions.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SimpleOpenApiTest {

    @Test
    void circuitBreaker() throws IOException {
        Path generatedRestClient = Paths.get("target", "generated-sources", "open-api-json", "org", "openapi",
                "quarkus", "simple_openapi_json", "api", "DefaultApi.java");

        assertThat(generatedRestClient)
                .exists()
                .isRegularFile()
                .content().isNotEmpty();

        CompilationUnit compilationUnit = StaticJavaParser.parse(generatedRestClient);

        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).stream()
                .map(c -> c.getAnnotationByClass(RegisterRestClient.class)).filter(Optional::isPresent).map(Optional::get)
                .map(a -> a.asNormalAnnotationExpr().getPairs())
                .forEach(n -> n.forEach(p -> assertNotEquals("baseUri", p.getName())));

        List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
        assertThat(methodDeclarations).isNotEmpty();

        Optional<MethodDeclaration> byeMethod = methodDeclarations.stream()
                .filter(m -> m.getNameAsString().equals("byeGet"))
                .findAny();

        assertThat(byeMethod).isNotEmpty();

        assertThat(byeMethod.orElseThrow())
                .hasCircuitBreakerAnnotation()
                .doesNotHaveAnyCircuitBreakerAttribute();

        methodDeclarations.stream()
                .filter(method -> !method.getNameAsString().equals("byeGet"))
                .forEach(method -> assertThat(method).doesNotHaveCircuitBreakerAnnotation());
    }
}
