package io.quarkiverse.openapi.generator.it.multipart.clientform;

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockMultipartClientForm implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/multipart"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"success\",\"message\":\"File uploaded\"}")
                        .withStatus(200)));

        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/simple-multipart"))
                .willReturn(WireMock.aResponse()
                        .withStatus(204)));

        return Collections.singletonMap("org.acme.openapi.multipart.clientform.api.MultipartApi/mp-rest/url",
                wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }

    @Override
    public void inject(Object testInstance) {
        if (testInstance instanceof MultipartClientFormTest) {
            ((MultipartClientFormTest) testInstance).multipartServer = wireMockServer;
        }
    }
}
