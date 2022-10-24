package io.quarkiverse.openapi.generator.it.security;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockOpenWeather implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    public static final String URL_KEY = "quarkus.rest-client.open_weather_yaml.url";
    public static final String URL_NO_SEC_KEY = "quarkus.rest-client.open_weather_no_security_yaml.url";

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8888);
        wireMockServer.start();

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/data/2.5/weather"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\"name\": \"Nowhere\"}")));
        return Map.of(URL_KEY, wireMockServer.baseUrl().concat("/data/2.5"),
                URL_NO_SEC_KEY, wireMockServer.baseUrl().concat("/data/2.5"));
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(wireMockServer, f -> f.getName().equals("openWeatherServer"));
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
