package io.quarkiverse.openapi.generator.it.security;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import jakarta.inject.Inject;

import org.acme.openapi.foo.api.FooResourceApi;
import org.acme.openapi.foo.model.FooDTO;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(WiremockFoo.class)
@QuarkusTest
public class AuthorizationHeaderApiKeyCanFilterWithoutDuplicateAuthorizationTest {

    WireMockServer fooServer;

    @RestClient
    @Inject
    FooResourceApi fooResourceApi;

    @Test
    public void testNoMultipleAuthorizationHeadersAreSent() {
        List<FooDTO> foos = fooResourceApi.getFoosUsingGET("not the fooapikey",
                123465L);
        assertNotNull(foos);
        fooServer.verify(WireMock.getRequestedFor(
                WireMock.urlEqualTo("/api/foo/v2.0/foo?something=123465"))
                .withHeader("Authorization", equalTo("fooapikey"))

        );
    }
}
