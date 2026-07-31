package io.quarkiverse.openapi.generator.it.pathencoding;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.it.pathencoding.api.api.DefaultApi;
import io.quarkiverse.openapi.generator.it.pathencoding.api.model.Resource;
import io.quarkiverse.openapi.generator.it.pathencoding.api.model.StockProfit;
import io.quarkiverse.openapi.generator.it.pathencoding.api.model.User;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PathEncodingTest {

    @Inject
    @RestClient
    DefaultApi api;

    @TestHTTPResource
    URL testUrl;

    @Test
    void testEmailPathParameterIsNotDoubleEncoded() {
        User result = api.getUserByEmail("test@test.com");
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void testSimplePathParameter() {
        Resource result = api.getResourceById("my-resource-123");
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("my-resource-123");
    }

    @Test
    void testPathParameterWithSpaces() {
        Resource result = api.getResourceById("my resource");
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("my resource");
    }

    @Test
    void testPathParameterWithPlus() {
        Resource result = api.getResourceById("test+value");
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("test+value");
    }

    @Test
    void testQueryParamNotDroppedWithPathParam() {
        StockProfit result = api.getStockProfit("KGTO", new java.math.BigDecimal("110"));
        assertThat(result).isNotNull();
        assertThat(result.getSymbol()).isEqualTo("KGTO");
        assertThat(result.getProfit()).isEqualTo("10%");
    }

    /**
     * Verifies issue #1738 fix: when the client is built programmatically via RestClientBuilder
     * (as Kogito's OpenApiWorkItemHandler does), query params must not be dropped.
     * PathParamEncodingParamConverterProvider was removed entirely because upstream bugs
     * in ProxyInvocationHandler made it silently null out non-String parameters.
     */
    @Test
    void testQueryParamNotDroppedViaRestClientBuilder() throws Exception {
        DefaultApi client = RestClientBuilder.newBuilder()
                .baseUri(testUrl.toURI())
                .build(DefaultApi.class);

        StockProfit result = client.getStockProfit("KGTO", new java.math.BigDecimal("110"));
        assertThat(result).isNotNull();
        assertThat(result.getSymbol()).isEqualTo("KGTO");
        assertThat(result.getProfit()).isEqualTo("10%");
    }
}
