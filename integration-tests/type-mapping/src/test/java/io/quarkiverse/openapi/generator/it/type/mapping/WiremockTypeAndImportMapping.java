package io.quarkiverse.openapi.generator.it.type.mapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockTypeAndImportMapping implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8890);
        wireMockServer.start();

        wireMockServer.stubFor(post(anyUrl())
                .willReturn(aResponse().withStatus(204)));
        return Collections.singletonMap("org.acme.openapi.typemapping.api.TypeMappingApi/mp-rest/url",
                wireMockServer.baseUrl());
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(wireMockServer, f -> f.getName().equals("typeMappingServer"));
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
