package io.quarkiverse.openapi.generator.configkey;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.config_key_with_dash_yaml.api.DefaultApi;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class QuarkusConfigKeyWithDashOpenApiTest {

    @RestClient
    @Inject
    DefaultApi defaultApi;

    @Test
    void apiIsBeingGenerated() {
        assertThat(defaultApi).isNotNull();
    }

    @Test
    void config_key_should_have_dash() {
        assertThat(DefaultApi.class.getAnnotation(RegisterRestClient.class)).isNotNull();
        assertThat(DefaultApi.class.getAnnotation(RegisterRestClient.class).configKey()).isEqualTo("my-api");
    }
}
