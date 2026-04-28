package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import static org.assertj.core.api.Assertions.assertThat;

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
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.NameExpr;

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

    @Test
    @DisplayName("Should generate SmallRye profile extensions on resource methods")
    void should_generate_smallrye_profile_extensions() throws IOException, ParseException {

        // arrange
        Path path = findOpenAPIPath("acme.json");

        OpenAPIToolsGenerator openAPIToolsGenerator = new OpenAPIToolsGenerator(
                new QuarkusJavaServerCodegenConfigurator()
                        .withInputBaseDir(path.toString())
                        .withOutputDir(Files.createTempDirectory("").toString())
                        .withBasePackage("org.acme"));

        // act
        List<File> files = openAPIToolsGenerator.generate();

        File petResource = files.stream()
                .filter(file -> file.getName().equals("PetResource.java"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("PetResource.java was not generated"));

        CompilationUnit cu = JavaParser.parse(petResource);

        MethodDeclaration taggedMethod = cu.getTypes().stream()
                .flatMap(type -> type.getMembers().stream())
                .filter(member -> member instanceof MethodDeclaration)
                .map(member -> (MethodDeclaration) member)
                .filter(method -> method.getName().equals("findPetsByTags"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("findPetsByTags method was not generated"));

        MethodDeclaration untaggedMethod = cu.getTypes().stream()
                .flatMap(type -> type.getMembers().stream())
                .filter(member -> member instanceof MethodDeclaration)
                .map(member -> (MethodDeclaration) member)
                .filter(method -> method.getName().equals("getPetById"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("getPetById method was not generated"));

        // assert
        Assertions.assertThat(taggedMethod.getAnnotations().stream()
                .map(Object::toString)
                .filter(annotation -> annotation.contains("Extensions"))
                .findFirst())
                .isPresent()
                .hasValueSatisfying(annotation -> {
                    Assertions.assertThat(annotation)
                            .contains("@org.eclipse.microprofile.openapi.annotations.extensions.Extensions");
                    Assertions.assertThat(annotation)
                            .contains("@org.eclipse.microprofile.openapi.annotations.extensions.Extension");
                    Assertions.assertThat(annotation).contains("name = \"x-smallrye-profile-admin\"");
                    Assertions.assertThat(annotation).contains("name = \"x-smallrye-profile-order\"");
                    Assertions.assertThat(annotation).contains("name = \"x-smallrye-profile-user\"");
                    Assertions.assertThat(annotation).contains("value = \"\"");
                });

        Assertions.assertThat(untaggedMethod.getAnnotations().stream()
                .map(Object::toString)
                .anyMatch(annotation -> annotation.contains("Extensions"))).isFalse();
    }

    @Test
    @DisplayName("Should generate class extra annotation if present")
    void should_generate_class_extra_annotation() throws IOException, ParseException {
        // arrange
        Path path = findOpenAPIPath("x-class-extra-annotation-openapi.json");

        OpenAPIToolsGenerator openAPIToolsGenerator = new OpenAPIToolsGenerator(
                new QuarkusJavaServerCodegenConfigurator()
                        .withInputBaseDir(path.toString())
                        .withOutputDir(Files.createTempDirectory("").toString())
                        .withBasePackage("org.acme"));

        // act
        List<File> files = openAPIToolsGenerator.generate();

        File model = files.stream()
                .filter(file -> file.getName().equals("HelloModel.java"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("HelloModel.java was not generated"));

        CompilationUnit cu = JavaParser.parse(model);
        List<TypeDeclaration> types = cu.getTypes();

        // assert
        assertThat(types).hasSize(1);
        assertThat(types.get(0).getAnnotations().stream().map(AnnotationExpr::getName).map(NameExpr::getName))
                .contains("AnAnnotation");
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
