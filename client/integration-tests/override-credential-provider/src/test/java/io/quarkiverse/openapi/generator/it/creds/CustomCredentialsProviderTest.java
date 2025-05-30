package io.quarkiverse.openapi.generator.it.creds;

import jakarta.inject.Inject;

import org.assertj.core.api.Assertions;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.simple_server_yaml.api.DefaultApi;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;

@WithTestResource(SimpleServerMockResource.class)
@QuarkusTest
public class CustomCredentialsProviderTest {

    /**
     * @see SimpleServerMockResource#inject(QuarkusTestResourceLifecycleManager.TestInjector)
     */
    WireMockServer simpleServer;

    @RestClient
    @Inject
    DefaultApi defaultApi;

    /**
     * This test validates whether the custom CredentialsProvider is override.
     */
    @Test
    public void testGetHardCodedToken() {
        Assertions.assertThat(defaultApi.getWithSimpleBearerTokenSecurityScheme().getStatus()).isEqualTo(200);

        simpleServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/simple")).withHeader("Authorization",
                WireMock.equalTo("Bearer " + CustomCredentialsProvider.TOKEN)));
    }

}