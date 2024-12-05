package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Collections;
import java.util.Map;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(PolymorphismTest.MockServer.class)
class PolymorphismTest {

    @RestClient
    @Inject
    org.openapi.quarkus.polymorphism_json.api.DefaultApi api;

    @Test
    void apiIsBeingGenerated() {

        org.openapi.quarkus.polymorphism_json.model.Data data = api.get();

        data.getThings()
                .forEach(it -> assertInstanceOf(org.openapi.quarkus.polymorphism_json.model.Thing.class, it));
        assertInstanceOf(org.openapi.quarkus.polymorphism_json.model.SomeThing.class, data.getThings().get(0));
        assertInstanceOf(org.openapi.quarkus.polymorphism_json.model.OtherThing.class, data.getThings().get(1));
    }

    public static class MockServer implements QuarkusTestResourceLifecycleManager {

        private WireMockServer wireMockServer;

        @Override
        public Map<String, String> start() {
            configureWiremockServer();
            return Collections.singletonMap("org.openapi.quarkus.polymorphism_json.api.DefaultApi/mp-rest/url",
                    wireMockServer.baseUrl());
        }

        private void configureWiremockServer() {
            var wireMockConfiguration = WireMockConfiguration.wireMockConfig().dynamicPort();
            wireMockServer = new WireMockServer(wireMockConfiguration);
            wireMockServer.start();

            wireMockServer.stubFor(get(urlEqualTo("/"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(
                                    "{\"things\":[{\"@type\":\"SomeThing\",\"thing\":\"thing\",\"some\":\"some\"},{\"@type\":\"OtherThing\",\"thing\":\"thing\",\"other\":\"other\"}]}")
                            .withTransformers("response-template")));
        }

        @Override
        public void stop() {
            if (wireMockServer != null) {
                wireMockServer.stop();
            }
        }
    }
}
