package io.quarkiverse.openapi.generator.it.pathencoding;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.it.pathencoding.api.api.DefaultApi;
import io.quarkiverse.openapi.generator.it.pathencoding.api.model.Resource;
import io.quarkiverse.openapi.generator.it.pathencoding.api.model.User;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PathEncodingTest {

    @Inject
    @RestClient
    DefaultApi api;

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
}
