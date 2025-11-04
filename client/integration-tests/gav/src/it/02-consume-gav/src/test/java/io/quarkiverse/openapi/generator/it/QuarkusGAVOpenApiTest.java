package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.*;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

class QuarkusGAVOpenApiTest {
    @Test
    void echo1IsBeingGenerated() {
        assertThatCode(
                () -> Class.forName(
                        "org.openapi.quarkus.io_quarkiverse_openapi_generator_quarkus_openapi_generator_gav_source_echo1.api.DefaultApi"))
                .doesNotThrowAnyException();
    }

    @Test
    void echoModelIsBeingGeneratedWithSerializableInterface() {
        assertThatCode(() -> {
            Class<?> apiClass = Class.forName(
                    "org.openapi.quarkus.io_quarkiverse_openapi_generator_quarkus_openapi_generator_gav_source_echo1.model.Echo");
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
