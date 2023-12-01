package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockCookieAuthentication implements QuarkusTestResourceLifecycleManager {
    private static final String URL_KEY = "quarkus.rest-client.cookie_authentication_json.url";

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        wireMockServer.stubFor(get(urlPathEqualTo("/v1/test"))
                .willReturn(aResponse().withStatus(204)));

        return Map.of(URL_KEY, wireMockServer.baseUrl());
    }

    @Override
    public void inject(final TestInjector testInjector) {
        testInjector.injectIntoFields(wireMockServer, f -> f.getName().equals("wireMockServer"));
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
