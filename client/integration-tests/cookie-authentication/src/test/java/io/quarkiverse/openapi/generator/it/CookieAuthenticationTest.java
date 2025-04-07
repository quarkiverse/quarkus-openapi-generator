package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.cookie_authentication_json.api.TestApi;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(WiremockCookieAuthentication.class)
@Tag("resteasy-reactive")
class CookieAuthenticationTest {
    // Injected by Quarkus test resource
    WireMockServer wireMockServer;

    @RestClient
    @Inject
    TestApi testApi;

    @Test
    void apiIsBeingGenerated() {
        final Response response = testApi.doTest(null)
                .await()
                .atMost(Duration.ofSeconds(5L));
        assertThat(response)
                .extracting(Response::getStatus)
                .isEqualTo(204);
        wireMockServer.verify(getRequestedFor(urlEqualTo("/v1/test")));
    }
}
