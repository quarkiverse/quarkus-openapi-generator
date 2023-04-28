package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.openapi_yaml.api.GetTestApi;
import org.openapi.quarkus.openapi_yaml.model.ResponseDto;
import org.openapi.quarkus.openapi_yaml.model.TestObj;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(WiremockBeanParam.class)
@QuarkusTest
class BeanParamOpenApiTest {

    @RestClient
    @Inject
    GetTestApi api;

    WireMockServer wireMockServer;

    @Test
    void apiIsBeingGenerated() {
        TestObj model = new TestObj();
        model.size(42);

        ResponseDto responseDto = api.getTest(model, true);
        assertThat(responseDto.getMessage()).isEqualTo("Hello");

        wireMockServer.verify(WireMock.getRequestedFor(
                WireMock.urlEqualTo("/get?size=42&unpaged=true")));
    }
}
