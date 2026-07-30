package io.quarkiverse.openapi.generator.it.multisegment;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestFilter;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.it.multisegment.api.api.DefaultApi;
import io.quarkiverse.openapi.generator.it.multisegment.api.model.GitReference;
import io.quarkiverse.openapi.generator.it.multisegment.api.model.User;
import io.quarkus.test.common.http.TestHTTPResource;
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

    @TestHTTPResource
    URL testUrl;

    @Test
    public void testMultiSegmentPathParameter() {
        // Use generated client to call: /repos/myorg/heads/feature-a
        GitReference result = api.getRepoRef("myorg", "heads/feature-a", null, null);

        // Verify result
        assertThat(result).isNotNull();
        assertThat(result.getRef()).isEqualTo("refs/heads/feature-a");
        assertThat(result.getUrl()).contains("myorg");
        assertThat(result.getUrl()).contains("heads/feature-a");
    }

    @Test
    public void testMultiSegmentWithMultipleLevels() {
        // Use generated client to call: /repos/myorg/heads/team/feature-b
        GitReference result = api.getRepoRef("myorg", "heads/team/feature-b", null, null);

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
        GitReference result = api.getRepoRef("testorg", "tags/v1.0.0", null, null);

        // Verify we got a successful response (proving auth was sent)
        assertThat(result).isNotNull();
        assertThat(result.getRef()).isEqualTo("refs/tags/v1.0.0");
        assertThat(result.getUrl()).contains("testorg");
        assertThat(result.getUrl()).contains("tags/v1.0.0");
    }

    @Test
    public void testQueryParamsWithMultiSegmentPathParam() {
        GitReference result = api.getRepoRef("myorg", "heads/feature-a", 2, "active");

        assertThat(result).isNotNull();
        assertThat(result.getRef()).isEqualTo("refs/heads/feature-a");
        assertThat(result.getPage()).isEqualTo(2);
        assertThat(result.getFilter()).isEqualTo("active");
    }

    @Test
    public void testQueryParamsNotDroppedWithPathParam() {
        GitReference result = api.getRepoRef("myorg", "main", 1, "all");

        assertThat(result).isNotNull();
        assertThat(result.getRef()).isEqualTo("refs/main");
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getFilter()).isEqualTo("all");
    }

    /**
     * Verifies that query params are preserved when the client is built
     * programmatically via RestClientBuilder (issue #1738). This catches
     * regressions from any upstream ProxyInvocationHandler changes.
     */
    @Test
    public void testQueryParamsNotDroppedViaRestClientBuilder() throws Exception {
        DefaultApi client = RestClientBuilder.newBuilder()
                .baseUri(testUrl.toURI())
                .register((ClientRequestFilter) ctx -> {
                    java.util.List<Object> auth = new java.util.ArrayList<>();
                    auth.add("Bearer test-token-123");
                    ctx.getHeaders().put("Authorization", auth);
                })
                .build(DefaultApi.class);

        GitReference result = client.getRepoRef("myorg", "heads/feature-a", 2, "active");

        assertThat(result).isNotNull();
        assertThat(result.getRef()).isEqualTo("refs/heads/feature-a");
        assertThat(result.getPage()).isEqualTo(2);
        assertThat(result.getFilter()).isEqualTo("active");
    }

}
