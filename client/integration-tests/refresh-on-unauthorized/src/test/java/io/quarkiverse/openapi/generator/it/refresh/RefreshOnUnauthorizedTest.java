package io.quarkiverse.openapi.generator.it.refresh;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

/**
 * End-to-end coverage for the {@code refresh-on-unauthorized} securityScheme option (issue #1389):
 * a {@code 401} response on an operation protected by an OAuth2/OIDC securityScheme should force the
 * <strong>next</strong> request made through that scheme's {@code OidcClient} to fetch a fresh token,
 * when the option is enabled. The first request that received the {@code 401} is not retried
 * automatically.
 */
@QuarkusTestResource(RefreshOnUnauthorizedMock.class)
@QuarkusTest
class RefreshOnUnauthorizedTest {

    @Test
    void secondCallSucceedsAfterForcedTokenRefreshWhenEnabled() {
        // First call: cold start, uses the (stale) cached token, the backend rejects it.
        given().post("/protected/refresh").then().statusCode(401);

        // Second call: refresh-on-unauthorized=true forces a fresh token, the backend accepts it.
        given().post("/protected/refresh").then().statusCode(200);
    }

    @Test
    void secondCallStillFailsWhenRefreshOnUnauthorizedIsDisabled() {
        // First call: same cold start as above, backend rejects the stale token.
        given().post("/protected/no-refresh").then().statusCode(401);

        // Second call: refresh-on-unauthorized is left at its default (false), so the client keeps
        // reusing the same cached (stale) token and keeps failing, exactly as before this feature existed.
        given().post("/protected/no-refresh").then().statusCode(401);
    }
}
