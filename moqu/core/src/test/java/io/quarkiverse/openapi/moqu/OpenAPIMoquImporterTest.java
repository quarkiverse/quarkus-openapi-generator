package io.quarkiverse.openapi.moqu;

import static io.quarkiverse.openapi.moqu.TestUtils.readContentFromFile;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.moqu.marshall.ObjectMapperFactory;

class OpenAPIMoquImporterTest {

    private final MoquImporter sut = new OpenAPIMoquImporter();

    @Test
    @DisplayName("Should create a new definition from OpenAPI specification")
    void shouldCreateANewDefinitionFromOpenAPISpecification() {
        // act
        String content = readContentFromFile("wiremock/one_example_in_the_same_path.yml");
        Moqu moqu = sut.parse(content);

        // assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(moqu.getRequestResponsePairs()).isNotEmpty();
            softly.assertThat(moqu.getRequestResponsePairs()).hasSize(1);
            softly.assertThat(moqu.getRequestResponsePairs().get(0)).satisfies(requestResponsePair -> {
                softly.assertThat(requestResponsePair.request().accept().name()).isEqualTo("Accept");
                softly.assertThat(requestResponsePair.request().accept().value()).contains("application/json");
                softly.assertThat(requestResponsePair.request().exampleName()).isEqualTo("john");
                softly.assertThat(requestResponsePair.request().parameters()).hasSize(1);
                softly.assertThat(requestResponsePair.request().parameters()).anySatisfy(parameters -> {
                    softly.assertThat(parameters.key()).isEqualTo("id");
                    softly.assertThat(parameters.value()).isEqualTo("1");
                });
            });
        });
    }

    @Test
    @DisplayName("Should throws exception when the OpenAPI is invalid")
    void shouldThrowsExceptionWhenTheOpenAPIIsInvalid() {
        // act, assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            sut.parse("""
                    openapi: 3.0.3
                    info:
                      version: 999-SNAPSHOT
                    """);
        });
    }

    @Test
    @DisplayName("Should handle OpenAPI with two path params")
    void shouldHandleOpenAPIWithTwoPathParams() {

        // act
        String content = readContentFromFile("wiremock/two_examples_in_the_same_path.yml");
        Moqu moqu = sut.parse(content);

        // assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(moqu.getRequestResponsePairs()).isNotEmpty();
            softly.assertThat(moqu.getRequestResponsePairs()).hasSize(2);
            softly.assertThat(moqu.getRequestResponsePairs()).allSatisfy(requestResponsePair -> {
                softly.assertThat(requestResponsePair.request().accept().name()).isEqualTo("Accept");
                softly.assertThat(requestResponsePair.request().accept().value()).contains("application/json");
            });
            softly.assertThat(moqu.getRequestResponsePairs()).anySatisfy(requestResponsePair -> {
                softly.assertThat(requestResponsePair.request().exampleName()).isEqualTo("john");
                softly.assertThat(requestResponsePair.request().parameters()).hasSize(1);
            });
            softly.assertThat(moqu.getRequestResponsePairs()).anySatisfy(requestResponsePair -> {
                softly.assertThat(requestResponsePair.request().exampleName()).isEqualTo("mary");
                softly.assertThat(requestResponsePair.request().parameters()).hasSize(1);
            });
        });
    }

    @Test
    @DisplayName("Should generate a response from ref")
    void shouldGenerateAResponseFromRef() {
        String content = readContentFromFile("wiremock/response_from_ref.yml");

        Moqu moqu = sut.parse(content);

        // assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(moqu.getRequestResponsePairs()).isNotEmpty();
            softly.assertThat(moqu.getRequestResponsePairs()).hasSize(1);
            softly.assertThat(moqu.getRequestResponsePairs().get(0)).satisfies(requestResponsePair -> {
                softly.assertThat(requestResponsePair.request().accept().name()).isEqualTo("Accept");
                softly.assertThat(requestResponsePair.request().accept().value()).contains("application/json");
                softly.assertThat(requestResponsePair.request().exampleName()).isEqualTo("quarkus");
                softly.assertThat(requestResponsePair.request().parameters()).hasSize(1);
                softly.assertThat(requestResponsePair.request().parameters()).anySatisfy(parameters -> {
                    softly.assertThat(parameters.key()).isEqualTo("id");
                    softly.assertThat(parameters.value()).isEqualTo("1");
                });
            });
        });
    }

    @Test
    @DisplayName("Should generate a response from ref as array")
    void shouldGenerateAResponseFromRefAsArray() {
        String content = readContentFromFile("wiremock/response_from_ref_array.yml");
        Moqu moqu = sut.parse(content);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(moqu.getRequestResponsePairs()).isNotEmpty();
            softly.assertThat(moqu.getRequestResponsePairs()).hasSize(1);
            softly.assertThat(moqu.getRequestResponsePairs().get(0)).satisfies(requestResponsePair -> {
                Map map = ObjectMapperFactory.getInstance().readValue(
                        requestResponsePair.response()
                                .content(),
                        Map.class);
                softly.assertThat((List<?>) map.get("versions"))
                        .hasSize(2);

                softly.assertThat(map.get("supportsJava")).isEqualTo(true);

            });
        });
    }

    @Test
    @DisplayName("Should generate a response from $ref and with no $ref")
    void shouldGenerateAResponseFromRefAndNoRef() {
        String content = readContentFromFile("wiremock/response_from_ref_and_noref.yml");
        Moqu moqu = sut.parse(content);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(moqu.getRequestResponsePairs()).isNotEmpty();
            softly.assertThat(moqu.getRequestResponsePairs()).hasSize(2);
            softly.assertThat(moqu.getRequestResponsePairs()).anySatisfy(requestResponsePair -> {
                Map map = ObjectMapperFactory.getInstance().readValue(
                        requestResponsePair.response()
                                .content(),
                        Map.class);
                softly.assertThat((List) map.get("versions"))
                        .hasSize(2);
            });

            softly.assertThat(moqu.getRequestResponsePairs()).anySatisfy(requestResponsePair -> {
                Map map = ObjectMapperFactory.getInstance().readValue(
                        requestResponsePair.response()
                                .content(),
                        Map.class);
                softly.assertThat((List) map.get("versions"))
                        .hasSize(1);
            });
        });
    }

    @Test
    @DisplayName("Should generate a full OpenAPI specification")
    void shouldGenerateAFullResponse() {
        String content = readContentFromFile("wiremock/full.yml");
        Moqu moqu = sut.parse(content);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(moqu.getRequestResponsePairs()).isNotEmpty();
            softly.assertThat(moqu.getRequestResponsePairs()).hasSize(1);
            softly.assertThat(moqu.getRequestResponsePairs().get(0)).satisfies(requestResponsePair -> {
                Map map = ObjectMapperFactory.getInstance().readValue(
                        requestResponsePair.response()
                                .content(),
                        Map.class);
                softly.assertThat((List<?>) map.get("versions"))
                        .hasSize(2);

                softly.assertThat(map.get("supportsJava")).isEqualTo(true);

                softly.assertThat(map.get("contributors")).isEqualTo(1000);

                softly.assertThat(((Map) map.get("rules")).get("hello")).isEqualTo("world");
            });
        });
    }
}
