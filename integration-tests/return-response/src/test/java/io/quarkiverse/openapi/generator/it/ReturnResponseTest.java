package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.Response;

import org.acme.openapi.api.ModelApi;
import org.acme.openapi.api.ResponseApi;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ReturnResponseTest {

    @Test
    void testModel() throws NoSuchMethodException {
        assertThat(ModelApi.class.getMethod("hello").getReturnType())
                .isEqualTo(String.class);
    }

    @Test
    void testResponse() throws NoSuchMethodException {
        assertThat(ResponseApi.class.getMethod("hello").getReturnType())
                .isEqualTo(Response.class);
    }
}
