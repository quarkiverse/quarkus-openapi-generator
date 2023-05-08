package io.quarkiverse.openapi.generator.it.security;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.acme.openapi.foo.api.FooResourceApi;
import org.acme.openapi.foo.model.FooDTO;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

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

        RequestPatternBuilder builder = getRequestedFor(
                urlEqualTo("/api/foo/v2.0/foo?something=123465"))
                .withHeader("Authorization", equalTo("fooapikey"));
        List<LoggedRequest> requestsWithAuthHeader = fooServer.findAll(builder);
        assertEquals(1, requestsWithAuthHeader.size(), "more than one request");

        LoggedRequest loggedRequest = requestsWithAuthHeader.get(0);
        HttpHeaders httpHeaders = loggedRequest.getHeaders();
        long authHeaderCount = httpHeaders.all().stream().filter(
                httpHeader -> httpHeader.keyEquals("Authorization")).count();
        assertEquals(1, authHeaderCount, "multiple Authorization headers found");
    }
}
