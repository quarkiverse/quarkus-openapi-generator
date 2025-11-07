package io.quarkiverse.openapi.generator.it;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SuppressWarningsTest {

    @Test
    void testSuppressWarningsAnnotationsExist() throws IOException {
        List<Path> javaFiles = getJavaFilesInDirectory("target/generated-sources/open-api/org/acme/suppresswarnings");

        assertFalse(javaFiles.isEmpty(), "No Java files found to verify");

        List<String> violations = getClassesWithNoSuppressWarningsAnnotation(javaFiles);

        assertTrue(violations.isEmpty(),
                "Classes missing @SuppressWarnings:\n" + String.join("\n", violations));
    }

    /**
     * Negative test to ensure that the checker correctly identifies classes missing the
     * \@SuppressWarnings annotation.
     */
    @Test
    void testSuppressWarningsCheckerFailsIfAnnotationMissing() throws IOException {
        List<Path> javaFiles = getJavaFilesInDirectory(
                "src/test/java/io/quarkiverse/openapi/generator/it/classwithoutsuppresswarningsexample");

        List<String> violations = getClassesWithNoSuppressWarningsAnnotation(javaFiles);

        assertFalse(violations.isEmpty(), "Expected violations for classes missing @SuppressWarnings");
    }

    private List<Path> getJavaFilesInDirectory(String directory) throws IOException {
        Path sourceRoot = Paths.get(directory);

        try (Stream<Path> pathStream = Files.walk(sourceRoot)) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();
        }
    }

    private List<String> getClassesWithNoSuppressWarningsAnnotation(List<Path> javaFiles) {
        List<String> violations = new ArrayList<>();

        // Java reflection on classes loaded from ClassLoader does not work since it strips
        // SuppressWarnings annotations during compilation. Gotta manually parse the source files.
        for (Path javaFile : javaFiles) {
            try {
                CompilationUnit cu = StaticJavaParser.parse(javaFile);

                // Check all class/interface declarations in the file (only checks top level classes)
                cu.getPrimaryType().ifPresent(typeDecl -> {
                    if (typeDecl.getAnnotationByName(SuppressWarnings.class.getSimpleName()).isEmpty()) {
                        violations.add(javaFile + " : " + typeDecl.getNameAsString() + " missing @SuppressWarnings");
                    }
                });

            } catch (Exception e) {
                violations.add(javaFile + " : Failed to parse - " + e.getMessage());
            }
        }
        return violations;
    }
}
