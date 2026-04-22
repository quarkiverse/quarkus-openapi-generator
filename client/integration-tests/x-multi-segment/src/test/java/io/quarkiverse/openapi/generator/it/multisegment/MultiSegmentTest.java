package io.quarkiverse.openapi.generator.it.multisegment;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.it.multisegment.api.api.DefaultApi;
import io.quarkiverse.openapi.generator.it.multisegment.api.model.GitReference;
import io.quarkiverse.openapi.generator.it.multisegment.api.model.User;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Integration test for x-multi-segment path parameter support.
 *
 * This test verifies that the generated REST client correctly handles
 * multi-segment path parameters using the generated DefaultApi client.
 */
@QuarkusTest
class MultiSegmentTest {

    @Inject
    @RestClient
    DefaultApi api;

    @Test
    public void testMultiSegmentPathParameter() {
        // Use generated client to call: /repos/myorg/heads/feature-a
        GitReference result = api.getRepoRef("myorg", "heads/feature-a");

        // Verify result
        assertThat(result).isNotNull();
        assertThat(result.getRef()).isEqualTo("refs/heads/feature-a");
        assertThat(result.getUrl()).contains("myorg");
        assertThat(result.getUrl()).contains("heads/feature-a");
    }

    @Test
    public void testMultiSegmentWithMultipleLevels() {
        // Use generated client to call: /repos/myorg/heads/team/feature-b
        GitReference result = api.getRepoRef("myorg", "heads/team/feature-b");

        // Verify result
        assertThat(result).isNotNull();
        assertThat(result.getRef()).isEqualTo("refs/heads/team/feature-b");
        assertThat(result.getUrl()).contains("myorg");
        assertThat(result.getUrl()).contains("heads/team/feature-b");
    }

    @Test
    public void testSingleSegmentParameter() {
        // Use generated client to call: /users/johndoe
        User result = api.getUser("johndoe");

        // Verify result
        assertThat(result).isNotNull();
        assertThat(result.getLogin()).isEqualTo("johndoe");
        assertThat(result.getId()).isEqualTo(12345);
    }

    @Test
    public void testAuthenticationIsApplied() {
        // Use generated client to call: /repos/testorg/tags/v1.0.0
        // The bearer token is configured in application.properties
        // If auth wasn't applied, mock server would return 401 and this would fail
        GitReference result = api.getRepoRef("testorg", "tags/v1.0.0");

        // Verify we got a successful response (proving auth was sent)
        assertThat(result).isNotNull();
        assertThat(result.getRef()).isEqualTo("refs/tags/v1.0.0");
        assertThat(result.getUrl()).contains("testorg");
        assertThat(result.getUrl()).contains("tags/v1.0.0");
    }
}
