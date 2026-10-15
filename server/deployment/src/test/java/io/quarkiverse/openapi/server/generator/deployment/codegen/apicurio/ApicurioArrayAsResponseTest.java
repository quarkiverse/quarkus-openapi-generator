package io.quarkiverse.openapi.server.generator.deployment.codegen.apicurio;

import static org.assertj.core.api.Assertions.assertThat;

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

class ApicurioArrayAsResponseTest {

    private static final Path INPUT_DIR = Path.of("src/test/resources");
    private static final String GLOBAL_PROPERTY = "quarkus.openapi.generator.server.array-as-response";
    private static final String SPEC_PROPERTY = "quarkus.openapi.generator.server.spec.array_as_response_yaml.array-as-response";

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Should return List<Model> for array responses by default")
    void should_return_list_of_models_by_default() throws Exception {
        Map<String, String> returnTypes = generate(Map.of());

        assertThat(returnTypes)
                .containsEntry("listAnimals", "List<Animal>")
                .containsEntry("listAnimalsFromNamedArray", "List<Animal>")
                .containsEntry("listAnimalsFromSharedResponse", "List<Animal>")
                .containsEntry("listAnimalsWithCustomReturnType", "Collection<String>")
                .containsEntry("listAnimalNames", "List<String>")
                .containsEntry("getAnimal", "Animal");
    }

    @Test
    @DisplayName("Should return Response for array of models responses when array-as-response is enabled")
    void should_return_response_when_enabled() throws Exception {
        Map<String, String> returnTypes = generate(Map.of(GLOBAL_PROPERTY, "true"));

        assertThat(returnTypes)
                .containsEntry("listAnimals", "Response")
                .containsEntry("listAnimalsFromNamedArray", "Response")
                .containsEntry("listAnimalsFromSharedResponse", "Response");
    }

    @Test
    @DisplayName("Should only change array of models responses when array-as-response is enabled")
    void should_not_change_other_responses_when_enabled() throws Exception {
        Map<String, String> returnTypes = generate(Map.of(GLOBAL_PROPERTY, "true"));

        assertThat(returnTypes)
                .containsEntry("listAnimalsWithCustomReturnType", "Collection<String>")
                .containsEntry("listAnimalNames", "List<String>")
                .containsEntry("getAnimal", "Animal");
    }

    @Test
    @DisplayName("Should enable array-as-response for a single specification")
    void should_enable_per_spec() throws Exception {
        Map<String, String> returnTypes = generate(Map.of(SPEC_PROPERTY, "true"));

        assertThat(returnTypes).containsEntry("listAnimals", "Response");
    }

    @Test
    @DisplayName("Should let the specification property override the global array-as-response property")
    void should_override_global_property_per_spec() throws Exception {
        Map<String, String> returnTypes = generate(Map.of(GLOBAL_PROPERTY, "true", SPEC_PROPERTY, "false"));

        assertThat(returnTypes).containsEntry("listAnimals", "List<Animal>");
    }

    @Test
    @DisplayName("Should wrap Response in CompletionStage when reactive and array-as-response are enabled")
    void should_wrap_response_when_reactive() throws Exception {
        Map<String, String> returnTypes = generate(Map.of(
                GLOBAL_PROPERTY, "true",
                "quarkus.openapi.generator.server.use-reactive", "true"));

        assertThat(returnTypes)
                .containsEntry("listAnimals", "CompletionStage<Response>")
                .containsEntry("getAnimal", "CompletionStage<Animal>");
    }

    @Test
    @DisplayName("Should let the return type configured for an operation override array-as-response")
    void should_let_operation_return_type_win() throws Exception {
        Map<String, String> returnTypes = generate(Map.of(
                GLOBAL_PROPERTY, "true",
                "quarkus.openapi.generator.server.operation-ids.listAnimals.return-type", "java.util.Set<String>"));

        assertThat(returnTypes)
                .containsEntry("listAnimals", "Set<String>")
                .containsEntry("listAnimalsFromNamedArray", "Response");
    }

    private Map<String, String> generate(Map<String, String> properties) throws Exception {
        Map<String, String> allProperties = new HashMap<>(properties);
        allProperties.put("quarkus.openapi.generator.server.use", "apicurio");
        allProperties.put("quarkus.openapi.generator.server.input-base-dir", "src/test/resources/array-as-response");
        Config config = ConfigProviderResolver.instance().getBuilder()
                .withSources(new PropertiesConfigSource(allProperties, "array-as-response-test", 100))
                .build();

        CodeGenContext context = new CodeGenContext(null, tempDir, tempDir, INPUT_DIR, false, config, true);
        new ApicurioOpenApiServerCodegen().trigger(context);

        Path resource = tempDir.resolve(Path.of("org", "acme", "AnimalsResource.java"));
        return StaticJavaParser.parse(resource.toFile()).findAll(MethodDeclaration.class).stream()
                .collect(Collectors.toMap(MethodDeclaration::getNameAsString, MethodDeclaration::getTypeAsString));
    }
}
