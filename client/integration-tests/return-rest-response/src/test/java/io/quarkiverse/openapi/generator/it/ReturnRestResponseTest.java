package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import org.acme.openapi.api.ReturnResponseTrueStringApi;
import org.acme.openapi.api.ReturnResponseTrueVoidApi;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ReturnResponseTest {

    @Test
    void testReturnResponseRestResponseString() throws NoSuchMethodException {
        var method = ReturnResponseTrueStringApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("org.jboss.resteasy.reactive.RestResponse<java.lang.String>");
    }

    @Test
    void testReturnResponseRestResponseVoid() throws NoSuchMethodException {
        var method = ReturnResponseTrueVoidApi.class.getMethod("hello");
        assertThat(method.getGenericReturnType().getTypeName())
                .isEqualTo("org.jboss.resteasy.reactive.RestResponse<java.lang.Void>");
    }
}
