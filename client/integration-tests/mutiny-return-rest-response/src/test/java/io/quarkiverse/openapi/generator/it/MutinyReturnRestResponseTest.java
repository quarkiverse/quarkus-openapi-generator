package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import org.acme.openapi.api.MutinyMultiReturnResponseRestResponseStringApi;
import org.acme.openapi.api.MutinyMultiReturnResponseRestResponseVoidApi;
import org.acme.openapi.api.MutinyReturnResponseRestResponseStringApi;
import org.acme.openapi.api.MutinyReturnResponseRestResponseVoidApi;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@Tag("resteasy-reactive")
class MutinyReturnResponseTest {
    @Test
    void testMutinyReturnResponseRestResponseString() throws NoSuchMethodException {
        var method = MutinyReturnResponseRestResponseStringApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<org.jboss.resteasy.reactive.RestResponse<java.lang.String>>");
    }

    @Test
    void testMutinyMultiReturnResponseRestResponseString() throws NoSuchMethodException {
        var method = MutinyMultiReturnResponseRestResponseStringApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Multi<org.jboss.resteasy.reactive.RestResponse<java.lang.String>>");
    }

    @Test
    void testMutinyReturnResponseRestResponseVoid() throws NoSuchMethodException {
        var method = MutinyReturnResponseRestResponseVoidApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Uni<org.jboss.resteasy.reactive.RestResponse<java.lang.Void>>");
    }

    @Test
    void testMutinyMultiReturnResponseRestResponseVoid() throws NoSuchMethodException {
        var method = MutinyMultiReturnResponseRestResponseVoidApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("io.smallrye.mutiny.Multi<org.jboss.resteasy.reactive.RestResponse<java.lang.Void>>");
    }
}
