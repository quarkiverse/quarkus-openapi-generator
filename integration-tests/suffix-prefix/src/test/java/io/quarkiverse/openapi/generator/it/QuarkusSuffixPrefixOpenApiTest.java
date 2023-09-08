package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.quarkus_suffix_prefix_openapi_yaml.api.ReactiveGreetingResourceCustomApiSuffix;
import org.openapi.quarkus.quarkus_suffix_prefix_openapi_yaml.model.CustomModelPrefixLinkCustomModelSuffix;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class QuarkusSuffixPrefixOpenApiTest {

    @RestClient
    @Inject
    ReactiveGreetingResourceCustomApiSuffix api;

    CustomModelPrefixLinkCustomModelSuffix customizedLink;

    @Test
    void apiIsBeingGenerated() {
        assertThat(api).isNotNull();
    }

}
