package io.quarkiverse.openapi.generator.configkey;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.empty_config_key_yaml.api.DefaultApi;

import com.oapi.pkg.api.ReactiveGreetingResourceApi;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

@QuarkusTest
@Tag("resteasy-reactive")
class QuarkusConfigKeyOpenApiTest {

    @RestClient
    @Inject
    ReactiveGreetingResourceApi api;

    @RestClient
    @Inject
    DefaultApi defaultApi;

    @Test
    void apiIsBeingGenerated() {
        assertThat(api).isNotNull();
    }

    @Test
    void config_key_apiIsBeingGenerated() throws NoSuchMethodException {
        assertThat(ReactiveGreetingResourceApi.class.getMethod("hello").getReturnType())
                .isEqualTo(Uni.class);
    }

    @Test
    void config_key_customAnnotation() {
        assertThat(ReactiveGreetingResourceApi.class.getAnnotation(CustomAnnotation.class)).isNotNull();
        assertThat(ReactiveGreetingResourceApi.class.getPackageName()).isEqualTo("com.oapi.pkg");
    }

    @Test
    void config_key_blank_shouldGetDefaultValue() {
        assertThat(DefaultApi.class.getAnnotation(RegisterRestClient.class)).isNotNull();
        assertThat(DefaultApi.class.getAnnotation(RegisterRestClient.class).configKey()).isEqualTo("empty_config_key_yaml");
    }
}
