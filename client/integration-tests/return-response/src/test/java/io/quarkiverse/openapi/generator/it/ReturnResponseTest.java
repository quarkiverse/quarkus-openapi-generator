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
        var method = ReturnResponseFalseStringApi.class.getMethod("hello");
        assertThat(method.getReturnType())
                .isEqualTo(String.class);
    }

    @Test
    void testReturnResponseTrueString() throws NoSuchMethodException {
        var method = ReturnResponseTrueStringApi.class.getMethod("hello");
        assertThat(method.getReturnType())
                .isEqualTo(Response.class);
    }

    @Test
    void testReturnResponseFalseVoid() throws NoSuchMethodException {
        var method = ReturnResponseFalseVoidApi.class.getMethod("hello");
        assertThat(method.getReturnType())
                .isEqualTo(Response.class);
    }

    @Test
    void testReturnResponseTrueVoid() throws NoSuchMethodException {
        var method = ReturnResponseTrueVoidApi.class.getMethod("hello");
        assertThat(method.getReturnType())
                .isEqualTo(Response.class);
    }
}
