package io.quarkiverse.openapi.generator.deployment.wrapper;

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

import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.EnumConstantDeclaration;

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
}
