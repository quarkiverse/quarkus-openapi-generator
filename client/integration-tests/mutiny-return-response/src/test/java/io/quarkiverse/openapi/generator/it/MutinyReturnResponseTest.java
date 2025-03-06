package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import org.acme.openapi.api.MutinyMultiReturnResponseFalseStringApi;
import org.acme.openapi.api.MutinyMultiReturnResponseFalseVoidApi;
import org.acme.openapi.api.MutinyMultiReturnResponseResponseStringApi;
import org.acme.openapi.api.MutinyMultiReturnResponseResponseVoidApi;
import org.acme.openapi.api.MutinyMultiReturnResponseTrueStringApi;
import org.acme.openapi.api.MutinyMultiReturnResponseTrueVoidApi;
import org.acme.openapi.api.MutinyReturnResponseFalseStringApi;
import org.acme.openapi.api.MutinyReturnResponseFalseVoidApi;
import org.acme.openapi.api.MutinyReturnResponseResponseStringApi;
import org.acme.openapi.api.MutinyReturnResponseResponseVoidApi;
import org.acme.openapi.api.MutinyReturnResponseTrueStringApi;
import org.acme.openapi.api.MutinyReturnResponseTrueVoidApi;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class MutinyReturnResponseTest {
    @Test
    void testMutinyReturnResponseFalseString() throws NoSuchMethodException {
        var method = MutinyReturnResponseFalseStringApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<java.lang.String>");
    }

    @Test
    void testMutinyMultiReturnResponseFalseString() throws NoSuchMethodException {
        var method = MutinyMultiReturnResponseFalseStringApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Multi<java.lang.String>");
    }

    @Test
    void testMutinyReturnResponseTrueString() throws NoSuchMethodException {
        var method = MutinyReturnResponseTrueStringApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>");
    }

    @Test
    void testMutinyMultiReturnResponseTrueString() throws NoSuchMethodException {
        var method = MutinyMultiReturnResponseTrueStringApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Multi<jakarta.ws.rs.core.Response>");
    }

    @Test
    void testMutinyReturnResponseResponseString() throws NoSuchMethodException {
        var method = MutinyReturnResponseResponseStringApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>");
    }

    @Test
    void testMutinyMultiReturnResponseResponseString() throws NoSuchMethodException {
        var method = MutinyMultiReturnResponseResponseStringApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Multi<jakarta.ws.rs.core.Response>");
    }

    @Test
    void testMutinyReturnResponseFalseVoid() throws NoSuchMethodException {
        var method = MutinyReturnResponseFalseVoidApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>");
    }

    @Test
    void testMutinyMultiReturnResponseFalseVoid() throws NoSuchMethodException {
        var method = MutinyMultiReturnResponseFalseVoidApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Multi<jakarta.ws.rs.core.Response>");
    }

    @Test
    void testMutinyReturnResponseTrueVoid() throws NoSuchMethodException {
        var method = MutinyReturnResponseTrueVoidApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>");
    }

    @Test
    void testMutinyMultiReturnResponseTrueVoid() throws NoSuchMethodException {
        var method = MutinyMultiReturnResponseTrueVoidApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Multi<jakarta.ws.rs.core.Response>");
    }

    @Test
    void testMutinyReturnResponseResponseVoid() throws NoSuchMethodException {
        var method = MutinyReturnResponseResponseVoidApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<jakarta.ws.rs.core.Response>");
    }

    @Test
    void testMutinyMultiReturnResponseResponseVoid() throws NoSuchMethodException {
        var method = MutinyMultiReturnResponseResponseVoidApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Multi<jakarta.ws.rs.core.Response>");
    }
}
