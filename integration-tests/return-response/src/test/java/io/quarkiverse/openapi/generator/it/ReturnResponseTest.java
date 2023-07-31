package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.Response;

import org.acme.openapi.api.*;
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

    @Test
    void testMutinyReturnResponseFalseString() throws NoSuchMethodException {
        var method = MutinyReturnResponseFalseStringApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<java.lang.String>");
    }

    @Test
    void testMutinyReturnResponseTrueString() throws NoSuchMethodException {
        var method = MutinyReturnResponseTrueStringApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>");
    }

    @Test
    void testMutinyReturnResponseFalseVoid() throws NoSuchMethodException {
        var method = MutinyReturnResponseFalseVoidApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>");
    }

    @Test
    void testMutinyReturnResponseTrueVoid() throws NoSuchMethodException {
        var method = MutinyReturnResponseTrueVoidApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>");
    }
}
