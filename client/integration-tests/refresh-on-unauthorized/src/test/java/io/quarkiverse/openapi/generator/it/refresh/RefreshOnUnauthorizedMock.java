package io.quarkiverse.openapi.generator.it.refresh;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.HashMap;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * Starts two independent WireMock servers, each acting both as the OIDC token issuer and as the
 * "protected" API for one securityScheme (refresh_oauth2 / no_refresh_oauth2):
 * <ul>
 * <li>The token endpoint ({@code /token}) returns a {@link #STALE_TOKEN} on the first call, then a
 * {@link #FRESH_TOKEN} on every subsequent call (modeling the authority issuing a new token after being
 * asked again).</li>
 * <li>The protected endpoint ({@code /protected/call}) returns {@code 401} for requests carrying the
 * {@link #STALE_TOKEN} and {@code 200} for requests carrying the {@link #FRESH_TOKEN}.</li>
 * </ul>
 * This lets a test tell, purely from the HTTP status it gets back, whether the generated client reused the
 * cached token or forced a fresh one.
 */
public class RefreshOnUnauthorizedMock implements QuarkusTestResourceLifecycleManager {

    public static final String REFRESH_MOCK_URL = "refresh.mock.url";
    public static final String NO_REFRESH_MOCK_URL = "no-refresh.mock.url";

    static final String STALE_TOKEN = "STALE_TOKEN";
    static final String FRESH_TOKEN = "FRESH_TOKEN";
    static final String TOKEN_PATH = "/token";
    static final String PROTECTED_PATH = "/protected/call";
    private static final String REFRESHED_STATE = "REFRESHED";

    private WireMockServer refreshServer;
    private WireMockServer noRefreshServer;

    @Override
    public Map<String, String> start() {
        refreshServer = newProtectedServiceWithOidc();
        noRefreshServer = newProtectedServiceWithOidc();

        Map<String, String> properties = new HashMap<>();
        properties.put(REFRESH_MOCK_URL, refreshServer.baseUrl());
        properties.put(NO_REFRESH_MOCK_URL, noRefreshServer.baseUrl());
        return properties;
    }

    private static WireMockServer newProtectedServiceWithOidc() {
        WireMockServer server = new WireMockServer(options().dynamicPort());
        server.start();

        server.stubFor(post(urlEqualTo(TOKEN_PATH))
                .inScenario("token-lifecycle")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo(REFRESHED_STATE)
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody(tokenResponse(STALE_TOKEN))));

        server.stubFor(post(urlEqualTo(TOKEN_PATH))
                .inScenario("token-lifecycle")
                .whenScenarioStateIs(REFRESHED_STATE)
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody(tokenResponse(FRESH_TOKEN))));

        server.stubFor(post(urlEqualTo(PROTECTED_PATH))
                .withHeader(AUTHORIZATION, equalTo("Bearer " + STALE_TOKEN))
                .willReturn(aResponse().withStatus(401)));

        server.stubFor(post(urlEqualTo(PROTECTED_PATH))
                .withHeader(AUTHORIZATION, equalTo("Bearer " + FRESH_TOKEN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));

        return server;
    }

    private static String tokenResponse(String accessToken) {
        return """
                {
                    "access_token": "%s",
                    "expires_in": 300,
                    "refresh_expires_in": 0,
                    "token_type": "bearer"
                }
                """.formatted(accessToken);
    }

    @Override
    public void stop() {
        if (refreshServer != null) {
            refreshServer.stop();
        }
        if (noRefreshServer != null) {
            noRefreshServer.stop();
        }
    }
}
