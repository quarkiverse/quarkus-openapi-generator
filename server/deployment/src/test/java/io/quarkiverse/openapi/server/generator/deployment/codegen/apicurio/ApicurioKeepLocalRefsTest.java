package io.quarkiverse.openapi.server.generator.deployment.codegen.apicurio;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;

import io.quarkus.deployment.CodeGenContext;
import io.smallrye.config.PropertiesConfigSource;

class ApicurioKeepLocalRefsTest {

    private static final Path INPUT_DIR = Path.of("src/test/resources");
    private static final String GLOBAL_PROPERTY = "quarkus.openapi.generator.server.keep-local-refs";
    private static final String SPEC_PROPERTY = "quarkus.openapi.generator.server.spec.keep_local_refs_yaml.keep-local-refs";

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Should inline local refs by default")
    void should_inline_local_refs_by_default() throws Exception {
        Map<String, String> signatures = generate(Map.of());

        assertThat(signatures)
                .containsEntry("listAnimals", "Response listAnimals()")
                .containsEntry("getAnimal", "Response getAnimal(long id)")
                .containsEntry("createAnimal", "void createAnimal(InputStream data)")
                .containsEntry("listAnimalNames", "List<String> listAnimalNames()");
        assertThat(tempDir.resolve(Path.of("org", "acme", "beans", "Int64Id.java"))).doesNotExist();
    }

    @Test
    @DisplayName("Should generate the model types when keep-local-refs is enabled")
    void should_use_model_types_when_enabled() throws Exception {
        Map<String, String> signatures = generate(Map.of(GLOBAL_PROPERTY, "true"));

        assertThat(signatures)
                .containsEntry("listAnimals", "List<Animal> listAnimals()")
                .containsEntry("getAnimal", "Animal getAnimal(long id)")
                .containsEntry("createAnimal", "void createAnimal(Animal data)")
                .containsEntry("listAnimalNames", "List<String> listAnimalNames()");
        assertThat(tempDir.resolve(Path.of("org", "acme", "beans", "Int64Id.java"))).doesNotExist();
    }

    @Test
    @DisplayName("Should enable keep-local-refs for a single specification")
    void should_enable_per_spec() throws Exception {
        Map<String, String> signatures = generate(Map.of(SPEC_PROPERTY, "true"));

        assertThat(signatures).containsEntry("listAnimals", "List<Animal> listAnimals()");
    }

    @Test
    @DisplayName("Should let the specification property override the global keep-local-refs property")
    void should_override_global_property_per_spec() throws Exception {
        Map<String, String> signatures = generate(Map.of(GLOBAL_PROPERTY, "true", SPEC_PROPERTY, "false"));

        assertThat(signatures).containsEntry("listAnimals", "Response listAnimals()");
    }

    private Map<String, String> generate(Map<String, String> properties) throws Exception {
        Map<String, String> allProperties = new HashMap<>(properties);
        allProperties.put("quarkus.openapi.generator.server.use", "apicurio");
        allProperties.put("quarkus.openapi.generator.server.input-base-dir", "src/test/resources/keep-local-refs");
        Config config = ConfigProviderResolver.instance().getBuilder()
                .withSources(new PropertiesConfigSource(allProperties, "keep-local-refs-test", 100))
                .build();

        CodeGenContext context = new CodeGenContext(null, tempDir, tempDir, INPUT_DIR, false, config, true);
        new ApicurioOpenApiServerCodegen().trigger(context);

        Path resource = tempDir.resolve(Path.of("org", "acme", "AnimalsResource.java"));
        assertThat(resource).exists();
        return StaticJavaParser.parse(Files.readString(resource)).findAll(MethodDeclaration.class).stream()
                .collect(Collectors.toMap(MethodDeclaration::getNameAsString,
                        method -> method.getTypeAsString() + " " + method.getNameAsString() + "("
                                + method.getParameters().stream()
                                        .map(parameter -> parameter.getTypeAsString() + " " + parameter.getNameAsString())
                                        .collect(Collectors.joining(", "))
                                + ")"));
    }
}
