package io.quarkiverse.openapi.generator.it.security;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.quarkiverse.openapi.generator.it.security.KeycloakServiceMock.KEYCLOAK_ACCESS_TOKEN;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;

import jakarta.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class TokenPropagationExternalServicesMock implements QuarkusTestResourceLifecycleManager {

    public static final String AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN";
    public static final String SERVICE3_HEADER_TO_PROPAGATE = "SERVICE3_HEADER_TO_PROPAGATE";
    public static final String SERVICE3_AUTHORIZATION_TOKEN = "SERVICE3_AUTHORIZATION_TOKEN";
    public static final String SERVICE4_HEADER_TO_PROPAGATE = "SERVICE4_HEADER_TO_PROPAGATE";
    public static final String SERVICE4_AUTHORIZATION_TOKEN = "SERVICE4_AUTHORIZATION_TOKEN";
    public static final String TOKEN_PROPAGATION_EXTERNAL_SERVICE_MOCK_URL = "propagation-external-service-mock.url";
    private static final String BEARER = "Bearer ";
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenPropagationExternalServicesMock.class);
    private WireMockServer wireMockServer;

    private static void stubForExternalService(String tokenPropagationExternalServiceUrl, String authorizationToken) {
        stubFor(post(tokenPropagationExternalServiceUrl)
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo(BEARER + authorizationToken))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));
    }

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor(wireMockServer.port());
        LOGGER.info("Mocked Server started at {}", wireMockServer.baseUrl());

        // stub the token-propagation-external-service1 invocation with the expected token
        stubForExternalService("/token-propagation-external-service1/executeQuery1", AUTHORIZATION_TOKEN);

        // stub the token-propagation-external-service2 invocation with the expected token
        stubForExternalService("/token-propagation-external-service2/executeQuery2", AUTHORIZATION_TOKEN);

        // stub the token-propagation-external-service3 invocation with the expected token
        stubForExternalService("/token-propagation-external-service3/executeQuery3", SERVICE3_AUTHORIZATION_TOKEN);

        // stub the token-propagation-external-service4 invocation with the expected token
        stubForExternalService("/token-propagation-external-service4/executeQuery4", SERVICE4_AUTHORIZATION_TOKEN);

        // stub the token-propagation-external-service5 invocation with the expected token, no propagation is produced
        // in this case but the service must receive the token provided by Keycloak since it has oauth2 security
        // configured.
        stubForExternalService("/token-propagation-external-service5/executeQuery5", KEYCLOAK_ACCESS_TOKEN);

        return Map.of(TOKEN_PROPAGATION_EXTERNAL_SERVICE_MOCK_URL, wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
