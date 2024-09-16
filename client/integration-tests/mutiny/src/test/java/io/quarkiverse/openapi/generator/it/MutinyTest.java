package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.quarkus_multiple_endpoints_openapi_yaml.api.TestEndpointApi;
import org.openapi.quarkus.quarkus_multiple_endpoints_openapi_yaml.model.TestEndpoint2Request;
import org.openapi.quarkus.quarkus_multiple_endpoints_openapi_yaml.model.TestEndpoint3200Response;
import org.openapi.quarkus.quarkus_multiple_endpoints_openapi_yaml.model.TestEndpoint3Request;
import org.openapi.quarkus.quarkus_multiple_endpoints_wrong_configuration_openapi_yaml.api.TestEndpointWrongConfigurationApi;
import org.openapi.quarkus.quarkus_multiple_endpoints_wrong_configuration_openapi_yaml.model.TestEndpoint4200Response;
import org.openapi.quarkus.quarkus_simple_openapi_yaml.api.ReactiveGreetingResourceApi;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@QuarkusTest
@Tag("resteasy-reactive")
class MutinyTest {

    @Test
    void apiIsBeingGenerated() throws NoSuchMethodException {
        assertThat(ReactiveGreetingResourceApi.class.getMethod("hello").getReturnType())
                .isEqualTo(Uni.class);
    }

    @Test
    void apiIsBeingGeneratedWithDeclaredMutinyReturnsType() throws NoSuchMethodException {
        assertThat(TestEndpointApi.class.getMethod("testEndpoint1").getReturnType())
                .isEqualTo(Multi.class);
        assertThat(TestEndpointApi.class.getMethod("testEndpoint2", TestEndpoint2Request.class).getReturnType())
                .isEqualTo(Uni.class);
        assertThat(TestEndpointApi.class.getMethod("testEndpoint3", TestEndpoint3Request.class).getReturnType())
                .isEqualTo(TestEndpoint3200Response.class);
    }

    @Test
    void apiIsBeingGenerateWithWrongConfigurationdWithDeclaredMutinyReturnsType() throws NoSuchMethodException {
        assertThat(TestEndpointWrongConfigurationApi.class.getMethod("testEndpoint1").getReturnType())
                .isEqualTo(Multi.class);
        assertThat(TestEndpointWrongConfigurationApi.class.getMethod("testEndpoint2",
                org.openapi.quarkus.quarkus_multiple_endpoints_wrong_configuration_openapi_yaml.model.TestEndpoint2Request.class)
                .getReturnType())
                .isEqualTo(Uni.class);
        assertThat(TestEndpointWrongConfigurationApi.class.getMethod("testEndpoint3",
                org.openapi.quarkus.quarkus_multiple_endpoints_wrong_configuration_openapi_yaml.model.TestEndpoint3Request.class)
                .getReturnType())
                .isEqualTo(Uni.class);
        assertThat(TestEndpointWrongConfigurationApi.class.getMethod("testEndpoint4", Integer.class).getReturnType())
                .isEqualTo(TestEndpoint4200Response.class);
    }
}
