package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openapi.quarkus.quarkus_simple_openapi_yaml.api.ReactiveGreetingResourceApi;
import org.openapi.quarkus.quarkus_simple_openapi_yaml.model.CloudEvent;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class QuarkusSimpleOpenApiTest {

    @Test
    void apiIsBeingGenerated() throws NoSuchFieldException, NoSuchMethodException {
        assertThat(ReactiveGreetingResourceApi.class.getMethod("myCustomMethod")).isNotNull();
        assertThat(CloudEvent.CloudEventQueryParam.class.getField("myCustomQueryParamAttribute")).isNotNull();
    }
}
