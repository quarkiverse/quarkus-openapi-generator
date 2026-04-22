package io.quarkiverse.openapi.generator.it.multisegment;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.it.multisegment.api.api.DefaultApi;
import io.quarkiverse.openapi.generator.markers.OperationMarker;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Integration test for x-multi-segment path parameter support.
 *
 * This test verifies that:
 * 1. The OpenAPI spec with x-multi-segment parameters generates correct metadata
 * 2. The @OperationMarker annotations include multiSegmentParams
 * 3. The mock server can handle multi-segment paths correctly
 */
@QuarkusTest
class MultiSegmentTest {

    /**
     * Test that the generated API has correct @OperationMarker annotation with multiSegmentParams
     * for the getReference operation that has x-multi-segment: true on the ref parameter.
     */
    @Test
    void testGeneratedCodeHasMultiSegmentMetadata() throws Exception {
        Method getRefMethod = DefaultApi.class.getMethod("getReference", String.class, String.class, String.class);
        OperationMarker marker = getRefMethod.getAnnotation(OperationMarker.class);

        assertThat(marker).isNotNull();
        assertThat(marker.operationId()).isEqualTo("getReference");
        assertThat(marker.path()).isEqualTo("/repos/{owner}/{repo}/git/ref/{ref}");
        assertThat(marker.multiSegmentParams()).containsExactly("ref");
    }

    /**
     * Test that the generated API has @OperationMarker annotation WITHOUT multiSegmentParams
     * for the getUser operation that does NOT have x-multi-segment on any parameter.
     */
    @Test
    void testGeneratedCodeHasNoMultiSegmentMetadataForSingleSegment() throws Exception {
        Method getUserMethod = DefaultApi.class.getMethod("getUser", String.class);
        OperationMarker marker = getUserMethod.getAnnotation(OperationMarker.class);

        assertThat(marker).isNotNull();
        assertThat(marker.operationId()).isEqualTo("getUser");
        assertThat(marker.path()).isEqualTo("/users/{username}");
        assertThat(marker.multiSegmentParams()).isEmpty();
    }

    /**
     * Test that multi-segment ref paths work correctly with the mock server.
     * This tests the server-side handling of multi-segment paths.
     */
    @Test
    void testMultiSegmentPathsOnServer() {
        // Test single-level ref: heads/main
        given()
                .header("Authorization", "Bearer test-token-123")
                .when()
                .get("/repos/myorg/myrepo/git/ref/heads/main")
                .then()
                .statusCode(200)
                .body("ref", equalTo("refs/heads/main"));

        // Test multi-level ref: heads/feature/my-branch
        given()
                .header("Authorization", "Bearer test-token-123")
                .when()
                .get("/repos/myorg/myrepo/git/ref/heads/feature/my-branch")
                .then()
                .statusCode(200)
                .body("ref", equalTo("refs/heads/feature/my-branch"));

        // Test deeply nested ref: heads/team/feature/v2/my-branch
        given()
                .header("Authorization", "Bearer test-token-123")
                .when()
                .get("/repos/myorg/myrepo/git/ref/heads/team/feature/v2/my-branch")
                .then()
                .statusCode(200)
                .body("ref", equalTo("refs/heads/team/feature/v2/my-branch"));
    }

    /**
     * Test that single-segment username paths work correctly with the mock server.
     */
    @Test
    void testSingleSegmentPathsOnServer() {
        given()
                .header("Authorization", "Bearer test-token-123")
                .when()
                .get("/users/johndoe")
                .then()
                .statusCode(200)
                .body("login", equalTo("johndoe"));
    }

    /**
     * Test that authentication is enforced on multi-segment paths.
     */
    @Test
    void testAuthenticationOnMultiSegmentPaths() {
        // Without auth header - should get 401
        given()
                .when()
                .get("/repos/myorg/myrepo/git/ref/heads/main")
                .then()
                .statusCode(401);

        // With correct auth - should get 200
        given()
                .header("Authorization", "Bearer test-token-123")
                .when()
                .get("/repos/myorg/myrepo/git/ref/heads/main")
                .then()
                .statusCode(200);
    }

    /**
     * Test that authentication is enforced on single-segment paths.
     */
    @Test
    void testAuthenticationOnSingleSegmentPaths() {
        // Without auth header - should get 401
        given()
                .when()
                .get("/users/johndoe")
                .then()
                .statusCode(401);

        // With correct auth - should get 200
        given()
                .header("Authorization", "Bearer test-token-123")
                .when()
                .get("/users/johndoe")
                .then()
                .statusCode(200);
    }
}
