package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import japa.parser.JavaParser;
import japa.parser.ParseException;

class OpenAPIToolsServerCodegenTest {

    @Test
    @DisplayName("All generated Java files must starts with the configured base package")
    void should_generate_with_custom_base_package() throws IOException {

        // arrange
        Path path = findOpenAPIPath("acme.json");

        OpenAPIToolsGenerator openAPIToolsGenerator = new OpenAPIToolsGenerator(
                new QuarkusJavaServerCodegenConfigurator()
                        .withInputBaseDir(path.toString())
                        .withOutputDir(Files.createTempDirectory("").toString())
                        .withBasePackage("org.acme"));

        // act
        List<File> files = openAPIToolsGenerator.generate();

        List<File> allJavaFiles = files.stream()
                .filter(file -> file.getName().endsWith(".java"))
                .toList();

        long allJavaFilesContainingBasePackage = allJavaFiles.stream()
                .map(file -> {
                    try {
                        return JavaParser.parse(file);
                    } catch (ParseException | IOException e) {
                        throw new IllegalStateException("Unable to parse the content of " + file, e);
                    }
                }).filter(o -> o.getPackage().getName().toString().startsWith("org.acme"))
                .count();

        // assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(files).isNotEmpty();
            softly.assertThat(allJavaFilesContainingBasePackage).isEqualTo(allJavaFiles.size());
        });

    }

    @Test
    @DisplayName("Should generate bean validation annotations for model and resource")
    void should_generate_bean_validation_annotations() throws IOException {

        // arrange
        Path path = findOpenAPIPath("acme.json");

        OpenAPIToolsGenerator openAPIToolsGenerator = new OpenAPIToolsGenerator(
                new QuarkusJavaServerCodegenConfigurator()
                        .withBeanValidation(true)
                        .withInputBaseDir(path.toString())
                        .withOutputDir(Files.createTempDirectory("").toString())
                        .withBasePackage("org.acme"));

        // act
        List<File> files = openAPIToolsGenerator.generate();

        List<File> allModelJavaFilesWithJSR303 = files.stream()
                .filter(file -> file.toPath().endsWith(Paths.get("org", "acme", "model", file.getName())))
                .filter(file -> {
                    try {
                        byte[] bytes = Files.readAllBytes(file.toPath());
                        String fileString = new String(bytes);
                        return fileString.contains("@Valid");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        // assert
        Assertions.assertThat(allModelJavaFilesWithJSR303).isNotEmpty();
    }

    @Test
    @DisplayName("Should generate resources with mutiny (Uni)")
    void should_generate_resources_with_uni() throws IOException {

        // arrange
        Path path = findOpenAPIPath("acme.json");

        OpenAPIToolsGenerator openAPIToolsGenerator = new OpenAPIToolsGenerator(
                new QuarkusJavaServerCodegenConfigurator()
                        .withBeanValidation(true)
                        .withReactive(true)
                        .withInputBaseDir(path.toString())
                        .withOutputDir(Files.createTempDirectory("").toString())
                        .withBasePackage("org.acme"));

        // act
        List<File> files = openAPIToolsGenerator.generate();

        List<File> allResourcesWithMutiny = files.stream()
                .filter(file -> file.toPath().endsWith(Paths.get("org", "acme", "resources", file.getName())))
                .filter(file -> {
                    try {
                        byte[] bytes = Files.readAllBytes(file.toPath());
                        String fileString = new String(bytes);
                        return fileString.contains("io.smallrye.mutiny.Uni");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        // assert
        Assertions.assertThat(allResourcesWithMutiny).isNotEmpty();

    }

    private Path findOpenAPIPath(String specFileName) {
        URL url = this.getClass().getResource("/openapitools/" + specFileName);
        Objects.requireNonNull(url, "Could not find /openapi/" + specFileName);

        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            // this should never happen for a well-formed file URL
            throw new RuntimeException("Invalid URI for " + url, e);
        }
        return Paths.get(uri);

    }

}
