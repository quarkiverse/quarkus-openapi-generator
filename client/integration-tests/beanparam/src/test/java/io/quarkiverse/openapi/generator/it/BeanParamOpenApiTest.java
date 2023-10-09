package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.openapi_yaml.api.BeanParamTestApi;
import org.openapi.quarkus.openapi_yaml.model.PatchRequestDto;
import org.openapi.quarkus.openapi_yaml.model.ResponseDto;
import org.openapi.quarkus.openapi_yaml.model.TestObj.TestObjQueryParam;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(WiremockBeanParam.class)
@QuarkusTest
class BeanParamOpenApiTest {

    @RestClient
    @Inject
    BeanParamTestApi api;

    WireMockServer wireMockServer;

    @Test
    void getRequestReceivesQueryParam() {
        TestObjQueryParam model = new TestObjQueryParam();
        model.size(42);

        ResponseDto responseDto = api.getTest(model, true);
        assertThat(responseDto.getMessage()).isEqualTo("Hello");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/get?size=42&unpaged=true")));
    }

    @Test
    void patchRequestReceivesParamsAsBody() {
        PatchRequestDto requestModel = new PatchRequestDto();
        requestModel.setName("Max");
        requestModel.setAge(42);

        api.patchTest(requestModel);

        wireMockServer.verify(patchRequestedFor(urlEqualTo("/patch"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(" { \"name\": \"Max\", \"age\": 42 }")));
    }
}
