package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.*;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class QuarkusGAVOpenApiTest {
    @ParameterizedTest
    @ValueSource(strings = {"io_quarkiverse_openapi_generator_quarkus_openapi_generator_gav_source_echo1",
          "io_quarkiverse_openapi_generator_quarkus_openapi_generator_gav_source_splitted_echo_echo_yaml",
          "io_quarkiverse_openapi_generator_quarkus_openapi_generator_gav_source_selfcontained_echo_echo1_yaml"})
    void echo1IsBeingGenerated(String packageName) {
        assertThatCode(
              () -> Class.forName(
                    "org.openapi.quarkus." + packageName + ".api.DefaultApi"))
              .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"io_quarkiverse_openapi_generator_quarkus_openapi_generator_gav_source_echo1",
          "io_quarkiverse_openapi_generator_quarkus_openapi_generator_gav_source_selfcontained_echo_echo1_yaml"})
    void echoModelIsBeingGeneratedWithSerializableInterface(String packageName) {
        assertThatCode(() -> {
            Class<?> apiClass = Class.forName(
                  "org.openapi.quarkus." + packageName + ".model.Echo");
            assertThat(apiClass.getInterfaces()).contains(Serializable.class);
        }).doesNotThrowAnyException();
    }

    @Test
    void echo2IsBeingNotGenerated() {
        assertThatCode(
              () -> Class.forName(
                    "org.openapi.quarkus.io_quarkiverse_openapi_generator_quarkus_openapi_generator_gav_source_echo2.api.DefaultApi"))
              .isInstanceOf(ClassNotFoundException.class);
    }

    @Test
    void otherIsBeingNotGenerated() {
        assertThatCode(
              () -> Class.forName(
                    "org.openapi.quarkus.io_quarkiverse_openapi_generator_quarkus_openapi_generator_gav_source_other.api.DefaultApi"))
              .isInstanceOf(ClassNotFoundException.class);
    }
}
