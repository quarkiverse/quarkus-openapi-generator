package io.quarkiverse.openapi.generator.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test for BaseCompositeAuthenticationProvider to validate that when multiple
 * alternative security schemes are defined for an operation (OR requirement),
 * only one provider needs to be satisfied.
 */
public class BaseCompositeAuthenticationProviderTest {

    private OperationAuthInfo createOperation() {
        return OperationAuthInfo.builder()
                .withId("testOp")
                .withMethod("POST")
                .withPath("/api/test")
                .build();
    }

    private OperationAuthInfo createOtherOperation() {
        return OperationAuthInfo.builder()
                .withId("otherOp")
                .withMethod("GET")
                .withPath("/api/other")
                .build();
    }

    private ClientRequestContext createRequestContext(String method, String path) {
        ClientRequestContext requestContext = Mockito.mock(ClientRequestContext.class);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        when(requestContext.getHeaders()).thenReturn(headers);
        when(requestContext.getMethod()).thenReturn(method);
        when(requestContext.getUri()).thenReturn(URI.create("http://localhost" + path));
        return requestContext;
    }

    //region Provider setup helpers

    /**
     * Configures a mock provider to add an Authorization header when its filter method is called,
     * simulating a successful authentication.
     */
    private void givenProviderWillAuthenticate(AuthProvider provider, MultivaluedMap<String, Object> headers, String token) {
        try {
            Mockito.doAnswer(inv -> {
                headers.putSingle("Authorization", token);
                return null;
            }).when(provider).filter(any(ClientRequestContext.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Configures a mock provider to throw an exception when its filter method is called.
     */
    private void givenProviderWillFail(AuthProvider provider, RuntimeException exception) {
        try {
            Mockito.doThrow(exception).when(provider).filter(any(ClientRequestContext.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Configures a mock provider to throw an IOException when its filter method is called.
     */
    private void givenProviderWillFail(AuthProvider provider, IOException exception) {
        try {
            Mockito.doAnswer(inv -> {
                throw exception;
            }).when(provider).filter(any(ClientRequestContext.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //endregion

    //region Exception assertion helpers

    /**
     * Asserts that calling the composite filter throws an exception with the expected message.
     */
    private void assertFilterThrows(BaseCompositeAuthenticationProvider composite,
            ClientRequestContext requestContext, String expectedMessage) throws IOException {
        Exception thrown = assertThrows(Exception.class, () -> composite.filter(requestContext));
        assertEquals(expectedMessage, thrown.getMessage());
    }

    //endregion

    @Test
    void testOnlyOneAlternativeProviderIsCalled() throws IOException {
        // Create two providers that both apply to the same operation
        AuthProvider provider1 = Mockito.mock(AuthProvider.class);
        AuthProvider provider2 = Mockito.mock(AuthProvider.class);

        // Both providers apply to the operation
        OperationAuthInfo operation = createOperation();
        when(provider1.operationsToFilter()).thenReturn(List.of(operation));
        when(provider2.operationsToFilter()).thenReturn(List.of(operation));

        // Mock the request context
        ClientRequestContext requestContext = createRequestContext("POST", "/api/test");
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        // Set up provider1 to add an Authorization header (simulating successful auth)
        givenProviderWillAuthenticate(provider1, headers, "Bearer token1");

        // Create the composite provider
        BaseCompositeAuthenticationProvider composite = new BaseCompositeAuthenticationProvider(List.of(provider1, provider2));

        // Apply the filter
        composite.filter(requestContext);

        // Verify that provider1 was called once
        verify(provider1, times(1)).filter(any(ClientRequestContext.class));

        // Verify that provider2 was NOT called because provider1 already applied successfully
        verify(provider2, never()).filter(any(ClientRequestContext.class));

        // Verify that the Authorization header was set by provider1
        assertEquals("Bearer token1", headers.getFirst("Authorization"));
    }

    @Test
    void testBothProvidersAreCalledWhenFirstFails() throws IOException {
        // Create two providers that both apply to the same operation
        AuthProvider provider1 = Mockito.mock(AuthProvider.class);
        AuthProvider provider2 = Mockito.mock(AuthProvider.class);

        // Both providers apply to the operation
        OperationAuthInfo operation = createOperation();
        when(provider1.operationsToFilter()).thenReturn(List.of(operation));
        when(provider2.operationsToFilter()).thenReturn(List.of(operation));

        // Mock the request context
        ClientRequestContext requestContext = createRequestContext("POST", "/api/test");
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        // Set up provider1 to throw an exception (simulating missing configuration)
        givenProviderWillFail(provider1, new RuntimeException("Missing OAuth2 configuration"));
        givenProviderWillAuthenticate(provider2, headers, "Bearer token2");

        // Create the composite provider
        BaseCompositeAuthenticationProvider composite = new BaseCompositeAuthenticationProvider(List.of(provider1, provider2));

        // Apply the filter
        composite.filter(requestContext);

        // Verify that provider1 was called once
        verify(provider1, times(1)).filter(any(ClientRequestContext.class));

        // Verify that provider2 was called because provider1 threw an exception
        verify(provider2, times(1)).filter(any(ClientRequestContext.class));

        // Verify that the Authorization header was set by provider2
        assertEquals("Bearer token2", headers.getFirst("Authorization"));
    }

    @Test
    void testOnlyRelevantProviderIsCalled() throws IOException {
        // Create two providers that apply to different operations
        AuthProvider provider1 = Mockito.mock(AuthProvider.class);
        AuthProvider provider2 = Mockito.mock(AuthProvider.class);

        // Provider1 applies to /api/test, provider2 applies to /api/other
        OperationAuthInfo operation1 = createOperation();
        OperationAuthInfo operation2 = createOtherOperation();
        when(provider1.operationsToFilter()).thenReturn(List.of(operation1));
        when(provider2.operationsToFilter()).thenReturn(List.of(operation2));

        // Mock the request context for /api/test
        ClientRequestContext requestContext = createRequestContext("POST", "/api/test");
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        givenProviderWillAuthenticate(provider1, headers, "Bearer token1");

        // Create the composite provider
        BaseCompositeAuthenticationProvider composite = new BaseCompositeAuthenticationProvider(List.of(provider1, provider2));

        // Apply the filter
        composite.filter(requestContext);

        // Verify that provider1 was called
        verify(provider1, times(1)).filter(any(ClientRequestContext.class));

        // Verify that provider2 was NOT called because it doesn't apply to this operation
        verify(provider2, never()).filter(any(ClientRequestContext.class));

        // Verify that the Authorization header was set by provider1
        assertEquals("Bearer token1", headers.getFirst("Authorization"));
    }

    @Test
    void testStopsOnFirstSuccessfulProvider() throws IOException {
        // Create three providers for the same operation
        AuthProvider provider1 = Mockito.mock(AuthProvider.class);
        AuthProvider provider2 = Mockito.mock(AuthProvider.class);
        AuthProvider provider3 = Mockito.mock(AuthProvider.class);

        OperationAuthInfo operation = createOperation();
        when(provider1.operationsToFilter()).thenReturn(List.of(operation));
        when(provider2.operationsToFilter()).thenReturn(List.of(operation));
        when(provider3.operationsToFilter()).thenReturn(List.of(operation));

        ClientRequestContext requestContext = createRequestContext("POST", "/api/test");
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        // All providers succeed, but only the first should be called
        givenProviderWillAuthenticate(provider1, headers, "Bearer token1");

        BaseCompositeAuthenticationProvider composite = new BaseCompositeAuthenticationProvider(
                List.of(provider1, provider2, provider3));

        composite.filter(requestContext);

        verify(provider1, times(1)).filter(any(ClientRequestContext.class));
        verify(provider2, never()).filter(any(ClientRequestContext.class));
        verify(provider3, never()).filter(any(ClientRequestContext.class));
        assertEquals("Bearer token1", headers.getFirst("Authorization"));
    }

    @Test
    void testAllProvidersFail() throws IOException {
        // Create two providers that both fail
        AuthProvider provider1 = Mockito.mock(AuthProvider.class);
        AuthProvider provider2 = Mockito.mock(AuthProvider.class);

        OperationAuthInfo operation = createOperation();
        when(provider1.operationsToFilter()).thenReturn(List.of(operation));
        when(provider2.operationsToFilter()).thenReturn(List.of(operation));

        ClientRequestContext requestContext = createRequestContext("POST", "/api/test");

        // Both providers throw exceptions
        givenProviderWillFail(provider1, new RuntimeException("First provider failed"));
        givenProviderWillFail(provider2, new RuntimeException("Second provider failed"));

        BaseCompositeAuthenticationProvider composite = new BaseCompositeAuthenticationProvider(List.of(provider1, provider2));

        assertFilterThrows(composite, requestContext, "Second provider failed");

        verify(provider1, times(1)).filter(any(ClientRequestContext.class));
        verify(provider2, times(1)).filter(any(ClientRequestContext.class));
    }

    @Test
    void testIOExceptionIsReThrown() throws IOException {
        AuthProvider provider = Mockito.mock(AuthProvider.class);
        OperationAuthInfo operation = createOperation();
        when(provider.operationsToFilter()).thenReturn(List.of(operation));

        ClientRequestContext requestContext = createRequestContext("POST", "/api/test");

        givenProviderWillFail(provider, new IOException("Network error"));

        BaseCompositeAuthenticationProvider composite = new BaseCompositeAuthenticationProvider(List.of(provider));

        assertFilterThrows(composite, requestContext, "Network error");
    }
}
