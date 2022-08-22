package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static java.util.Collections.singletonMap;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockAWX implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    public static final String URL_KEY = "quarkus.rest-client.awx_json.url";

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8890);
        wireMockServer.start();
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v2/job_templates/7/launch/"))
                .willReturn(aResponse().withStatus(200)));
        return singletonMap(URL_KEY, wireMockServer.baseUrl());
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(wireMockServer, f -> f.getName().equals("awxServer"));
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
