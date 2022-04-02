package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockMultipart implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8889);
        wireMockServer.start();

        wireMockServer.stubFor(post(anyUrl())
                .willReturn(aResponse().withStatus(204)));
        return Collections.singletonMap("org.acme.openapi.multipart.api.UserProfileDataApi/mp-rest/url",
                wireMockServer.baseUrl());
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(wireMockServer, f -> f.getName().equals("multipartServer"));
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
