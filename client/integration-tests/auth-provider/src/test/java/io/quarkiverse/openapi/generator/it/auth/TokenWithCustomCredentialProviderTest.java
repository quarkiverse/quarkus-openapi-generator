package io.quarkiverse.openapi.generator.it.auth;

import static io.quarkiverse.openapi.generator.it.auth.TokenExternalServicesMock.AUTHORIZATION_TOKEN;
import static io.restassured.RestAssured.given;

import java.util.Map;

import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(TokenExternalServicesMock.class)
@QuarkusTestResource(KeycloakServiceMock.class)
@QuarkusTest
// Enabled only for RESTEasy Classic while https://github.com/quarkiverse/quarkus-openapi-generator/issues/434 is not fixed
@Tag("resteasy-classic")
class TokenWithCustomCredentialProviderTest {

    @ParameterizedTest
    @ValueSource(strings = { "service1", "service2", "service3", "service5" })
    void testService(String service) {
        Map<String, String> headers = Map.of(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TOKEN);

        given()
                .headers(headers)
                .post("/token_server/" + service)
                .then()
                .statusCode(200);
    }
}
