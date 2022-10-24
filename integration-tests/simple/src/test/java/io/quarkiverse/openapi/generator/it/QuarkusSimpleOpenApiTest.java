package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.quarkus_simple_openapi_yaml.api.ReactiveGreetingResourceApi;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class QuarkusSimpleOpenApiTest {

    @RestClient
    @Inject
    ReactiveGreetingResourceApi api;

    @Test
    void apiIsBeingGenerated() {
        assertThat(api).isNotNull();
    }
}
