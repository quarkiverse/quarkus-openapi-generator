package io.quarkiverse.openapi.generator.it.security;

import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.AUTHORIZATION_TOKEN;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE3_AUTHORIZATION_TOKEN;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE3_HEADER_TO_PROPAGATE;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE4_AUTHORIZATION_TOKEN;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE4_HEADER_TO_PROPAGATE;
import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(TokenPropagationExternalServicesMock.class)
@QuarkusTestResource(KeycloakServiceMock.class)
@QuarkusTest
class TokenPropagationTest {

    @ParameterizedTest
    @ValueSource(strings = { "service1", "service2", "service3", "service4", "service5" })
    void service1(String service) {
        Map<String, String> headers = new HashMap<>();
        // service token-propagation-external-service1 and token-propagation-external-service2 will receive the AUTHORIZATION_TOKEN
        headers.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TOKEN);
        // service token-propagation-external-service3 will receive the SERVICE3_AUTHORIZATION_TOKEN
        headers.put(SERVICE3_HEADER_TO_PROPAGATE, SERVICE3_AUTHORIZATION_TOKEN);
        // service token-propagation-external-service4 will receive the SERVICE4_AUTHORIZATION_TOKEN
        headers.put(SERVICE4_HEADER_TO_PROPAGATE, SERVICE4_AUTHORIZATION_TOKEN);

        given()
                .headers(headers)
                .post("/token_propagation/" + service)
                .then()
                .statusCode(200);
    }
}
