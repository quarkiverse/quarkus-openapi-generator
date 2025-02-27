package io.quarkiverse.openapi.moqu.wiremock.mapper;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.moqu.Moqu;
import io.quarkiverse.openapi.moqu.OpenAPIMoquImporter;
import io.quarkiverse.openapi.moqu.TestUtils;
import io.quarkiverse.openapi.moqu.wiremock.model.WiremockMapping;

public class WiremockPathParamTest {

    @Test
    @DisplayName("Should convert a OpenAPI with a single path param correctly")
    void shouldMapOneWiremockDefinition() {

        String content = TestUtils.readContentFromFile("wiremock/path_param_one_path_param.yml");
        if (content == null) {
            Assertions.fail("Was not possible to read the file!");
        }

        OpenAPIMoquImporter importer = new OpenAPIMoquImporter();

        Moqu mock = importer.parse(content);

        WiremockMapper wiremockMapper = new WiremockMapper();

        List<WiremockMapping> definitions = wiremockMapper.map(mock);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(definitions).hasSize(1);
            WiremockMapping definition = definitions.get(0);

            softly.assertThat(definition).satisfies(wiremockDefinition -> {
                // request
                softly.assertThat(wiremockDefinition.request().url()).isEqualTo("/users/1");
                softly.assertThat(wiremockDefinition.request().method()).isEqualTo("GET");
                // response
                softly.assertThat(wiremockDefinition.response().status()).isEqualTo(200);
                softly.assertThat(wiremockDefinition.response().body()).isEqualTo("{\"name\": \"Quarkus\"}");
            });
        });
    }

    @Test
    @DisplayName("Should convert with a two OpenAPI#paths each one with one path param")
    void shouldMapTwoWiremockDefinitions() {

        String content = TestUtils.readContentFromFile("wiremock/path_param_two_params_but_different_path.yml");
        if (content == null) {
            Assertions.fail("Was not possible to read the file!");
        }

        OpenAPIMoquImporter importer = new OpenAPIMoquImporter();

        Moqu mock = importer.parse(content);

        WiremockMapper wiremockMapper = new WiremockMapper();

        List<WiremockMapping> definitions = wiremockMapper.map(mock);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(definitions).hasSize(2);

            softly.assertThat(definitions).anySatisfy(wiremockDefinition -> {
                // request
                softly.assertThat(wiremockDefinition.request().url()).isEqualTo("/users/1");
                softly.assertThat(wiremockDefinition.request().method()).isEqualTo("GET");
                // response
                softly.assertThat(wiremockDefinition.response().status()).isEqualTo(200);
                softly.assertThat(wiremockDefinition.response().body()).isEqualTo("{\"name\": \"John Doe\"}");
            });

            softly.assertThat(definitions).anySatisfy(wiremockDefinition -> {
                // request
                softly.assertThat(wiremockDefinition.request().url()).isEqualTo("/frameworks/quarkus");
                softly.assertThat(wiremockDefinition.request().method()).isEqualTo("GET");
                // response
                softly.assertThat(wiremockDefinition.response().status()).isEqualTo(200);
                softly.assertThat(wiremockDefinition.response().body())
                        .isEqualTo("{\"description\": \"Quarkus, build time augmentation toolkit\"}");
            });
        });
    }

    @Test
    @DisplayName("Should convert with a combination of path param")
    void shouldConvertWithACombinationOfPathParam() {

        String content = TestUtils.readContentFromFile("wiremock/path_param_two_path_params_combination.yml");
        if (content == null) {
            Assertions.fail("Was not possible to read the file!");
        }

        OpenAPIMoquImporter importer = new OpenAPIMoquImporter();

        Moqu mock = importer.parse(content);

        WiremockMapper wiremockMapper = new WiremockMapper();

        List<WiremockMapping> definitions = wiremockMapper.map(mock);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(definitions).hasSize(2);

            softly.assertThat(definitions).anySatisfy(wiremockDefinition -> {
                // request
                softly.assertThat(wiremockDefinition.request().url()).isEqualTo("/users/1/books/80");
                softly.assertThat(wiremockDefinition.request().method()).isEqualTo("GET");
                // response
                softly.assertThat(wiremockDefinition.response().status()).isEqualTo(200);
                softly.assertThat(wiremockDefinition.response().body())
                        .isEqualTo("{\"name\": \"Book for John\", \"chapters\": 8}");
            });

            softly.assertThat(definitions).anySatisfy(wiremockDefinition -> {
                // request
                softly.assertThat(wiremockDefinition.request().url()).isEqualTo("/users/2/books/70");
                softly.assertThat(wiremockDefinition.request().method()).isEqualTo("GET");
                // response
                softly.assertThat(wiremockDefinition.response().status()).isEqualTo(200);
                softly.assertThat(wiremockDefinition.response().body())
                        .isEqualTo("{\"name\": \"Book for Mary\", \"chapters\": 10}");
            });
        });
    }

    @Test
    @DisplayName("Should convert with a combination but only one with example")
    void shouldConvertPathParamCombinationOnlyOneWithExample() {

        String content = TestUtils.readContentFromFile("wiremock/path_param_two_path_params_only_one_with_example.yml");
        if (content == null) {
            Assertions.fail("Was not possible to read the file!");
        }

        OpenAPIMoquImporter importer = new OpenAPIMoquImporter();

        Moqu mock = importer.parse(content);

        WiremockMapper wiremockMapper = new WiremockMapper();

        List<WiremockMapping> definitions = wiremockMapper.map(mock);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(definitions).hasSize(1);

            softly.assertThat(definitions).anySatisfy(wiremockDefinition -> {
                // request
                softly.assertThat(wiremockDefinition.request().url()).isEqualTo("/users/1/books/{bookId}");
                softly.assertThat(wiremockDefinition.request().method()).isEqualTo("GET");
                // response
                softly.assertThat(wiremockDefinition.response().status()).isEqualTo(200);
                softly.assertThat(wiremockDefinition.response().body())
                        .isEqualTo("{\"name\": \"Book for John\", \"chapters\": 8}");
            });
        });
    }

}
