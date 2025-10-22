package io.quarkiverse.openapi.generator.it;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class QuarkusGAVOpenApiTest {
    @Test
    void apiIsBeingGenerated() {
        assertThatCode(
              () -> Class.forName("org.openapi.quarkus.io_quarkiverse_openapi_generator_quarkus_openapi_generator_gav_source_echo1.api.DefaultApi"))
              .doesNotThrowAnyException();
    }

    @Test
    void apiIsBeingNotGenerated() {
        assertThatCode(
              () -> Class.forName("org.openapi.quarkus.io_quarkiverse_openapi_generator_quarkus_openapi_generator_gav_source_echo2.api.DefaultApi"))
              .isInstanceOf(ClassNotFoundException.class);
    }
}
