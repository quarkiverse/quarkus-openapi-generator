package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.Response;

import org.acme.openapi.api.ReturnResponseFalseStringApi;
import org.acme.openapi.api.ReturnResponseFalseVoidApi;
import org.acme.openapi.api.ReturnResponseTrueStringApi;
import org.acme.openapi.api.ReturnResponseTrueVoidApi;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ReturnResponseTest {

    @Test
    void testReturnResponseFalseString() throws NoSuchMethodException {
        assertThat(ReturnResponseFalseStringApi.class.getMethod("hello").getReturnType())
                .isEqualTo(String.class);
    }

    @Test
    void testReturnResponseTrueString() throws NoSuchMethodException {
        assertThat(ReturnResponseTrueStringApi.class.getMethod("hello").getReturnType())
                .isEqualTo(Response.class);
    }

    @Test
    void testReturnResponseFalseVoid() throws NoSuchMethodException {
        assertThat(ReturnResponseFalseVoidApi.class.getMethod("hello").getReturnType())
                .isEqualTo(Void.TYPE);
    }

    @Test
    void testReturnResponseTrueVoid() throws NoSuchMethodException {
        assertThat(ReturnResponseTrueVoidApi.class.getMethod("hello").getReturnType())
                .isEqualTo(Response.class);
    }
}
