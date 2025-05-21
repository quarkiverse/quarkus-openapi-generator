package io.quarkiverse.openapi.generator.it.auth;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.HashMap;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * Lightweight Keycloak mock to use when an OidcClient is required, and we don't want/need to start a full Keycloak
 * container as part of the tests, etc. Keep the things simple.
 */
public class KeycloakServiceMock implements QuarkusTestResourceLifecycleManager {

    public static final String KEY_CLOAK_SERVICE_URL = "keycloak.mock.service.url";
    public static final String KEY_CLOAK_SERVICE_TOKEN_PATH = "keycloak.mock.service.token-path";
    public static final String REALM = "kogito-tests";
    public static final String KEY_CLOAK_SERVICE_TOKEN_PATH_VALUE = "/realms/" + REALM + "/protocol/openid-connect/token";
    public static final String CLIENT_ID = "kogito-app";
    public static final String SECRET = "secret";
    public static final String KEYCLOAK_ACCESS_TOKEN = "KEYCLOAK_ACCESS_TOKEN";
    public static final String KEYCLOAK_REFRESH_TOKEN = "KEYCLOAK_REFRESH_TOKEN";
    public static final String KEYCLOAK_SESSION_STATE = "KEYCLOAK_SESSION_STATE";

    public static final String AUTH_REQUEST_BODY = "grant_type=client_credentials";

    private static final ThreadLocal<WireMockServer> wireMockServer = new ThreadLocal<>();

    @Override
    public Map<String, String> start() {
        wireMockServer.set(new WireMockServer(options().dynamicPort()));
        wireMockServer.get().start();
        configureFor(wireMockServer.get().port());

        stubFor(post(KEY_CLOAK_SERVICE_TOKEN_PATH_VALUE)
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_FORM_URLENCODED))
                .withBasicAuth(CLIENT_ID, SECRET)
                .withRequestBody(equalTo(AUTH_REQUEST_BODY))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody(getTokenResult())));

        Map<String, String> properties = new HashMap<>();
        properties.put(KEY_CLOAK_SERVICE_URL, wireMockServer.get().baseUrl());
        properties.put(KEY_CLOAK_SERVICE_TOKEN_PATH, KEY_CLOAK_SERVICE_TOKEN_PATH_VALUE);
        return properties;
    }

    private static String getTokenResult() {
        return """
                {
                    "access_token": "%s",
                    "expires_in": 300,
                    "refresh_expires_in": 1800,
                    "refresh_token": "%s",
                    "token_type": "bearer",
                    "not-before-policy": 0,
                    "session_state": "%s",
                    "scope": "email profile"
                }
                """.formatted(KEYCLOAK_ACCESS_TOKEN, KEYCLOAK_REFRESH_TOKEN, KEYCLOAK_SESSION_STATE);
    }

    @Override
    public void stop() {
        if (wireMockServer.get() != null) {
            wireMockServer.get().stop();
            wireMockServer.remove();
        }
    }
}
