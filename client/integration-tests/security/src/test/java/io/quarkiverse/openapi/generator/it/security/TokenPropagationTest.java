package io.quarkiverse.openapi.generator.it.security;

import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE1_AUTHORIZATION_TOKEN;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE2_AUTHORIZATION_TOKEN;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE3_AUTHORIZATION_TOKEN;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE3_HEADER_TO_PROPAGATE;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE4_AUTHORIZATION_TOKEN;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE4_HEADER_TO_PROPAGATE;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE6_AUTHORIZATION_TOKEN;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE7_AUTHORIZATION_TOKEN;
import static io.quarkiverse.openapi.generator.it.security.TokenPropagationExternalServicesMock.SERVICE7_HEADER_TO_PROPAGATE;
import static io.restassured.RestAssured.given;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(TokenPropagationExternalServicesMock.class)
@QuarkusTestResource(KeycloakServiceMock.class)
@QuarkusTest
// Enabled only for RESTEasy Classic while https://github.com/quarkiverse/quarkus-openapi-generator/issues/434 is not fixed
@Tag("resteasy-classic")
class TokenPropagationTest {

    private static class HeaderArgument {
        String headerName;
        String headerValue;

        HeaderArgument(String headerName, String headerValue) {
            this.headerName = headerName;
            this.headerValue = headerValue;
        }

    }

    @ParameterizedTest
    @MethodSource("serviceInvocationParams")
    void invokeService(String service, HeaderArgument headerArgument) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(headerArgument.headerName, Collections.singletonList(headerArgument.headerValue));
        given()
                .headers(headers)
                .post("/token_propagation/" + service)
                .then()
                .statusCode(200);
    }

    private static Stream<Arguments> serviceInvocationParams() {
        return Stream.of(
                // service token-propagation-external-service1 will receive the SERVICE1_AUTHORIZATION_TOKEN
                Arguments.of("service1", new HeaderArgument(HttpHeaders.AUTHORIZATION, SERVICE1_AUTHORIZATION_TOKEN)),
                // service token-propagation-external-service2 will receive the SERVICE2_AUTHORIZATION_TOKEN
                Arguments.of("service2", new HeaderArgument(HttpHeaders.AUTHORIZATION, SERVICE2_AUTHORIZATION_TOKEN)),
                // service token-propagation-external-service3 will receive the SERVICE3_AUTHORIZATION_TOKEN
                Arguments.of("service3", new HeaderArgument(SERVICE3_HEADER_TO_PROPAGATE, SERVICE3_AUTHORIZATION_TOKEN)),
                // service token-propagation-external-service4 will receive the SERVICE4_AUTHORIZATION_TOKEN
                Arguments.of("service4", new HeaderArgument(SERVICE4_HEADER_TO_PROPAGATE, SERVICE4_AUTHORIZATION_TOKEN)),
                // service token-propagation-external-service6-with-base-url will receive the SERVICE6_AUTHORIZATION_TOKEN
                Arguments.of("service6", new HeaderArgument(HttpHeaders.AUTHORIZATION, SERVICE6_AUTHORIZATION_TOKEN)),
                // service token-propagation-external-service7-with-base-url will receive the SERVICE7_AUTHORIZATION_TOKEN
                Arguments.of("service7", new HeaderArgument(SERVICE7_HEADER_TO_PROPAGATE, SERVICE7_AUTHORIZATION_TOKEN)));
    }
}
