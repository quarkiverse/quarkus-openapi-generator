package io.quarkiverse.openapi.generator.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BaseCompositeAuthenticationProviderTest {

    //region Provider setup helpers (alternative security scheme tests)

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
        lenient().when(requestContext.getHeaders()).thenReturn(headers);
        lenient().when(requestContext.getMethod()).thenReturn(method);
        lenient().when(requestContext.getUri()).thenReturn(URI.create("http://localhost" + path));
        return requestContext;
    }

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

    private void givenProviderWillFail(AuthProvider provider, RuntimeException exception) {
        try {
            Mockito.doThrow(exception).when(provider).filter(any(ClientRequestContext.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void givenProviderWillFail(AuthProvider provider, IOException exception) {
        try {
            Mockito.doAnswer(inv -> {
                throw exception;
            }).when(provider).filter(any(ClientRequestContext.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertFilterThrows(BaseCompositeAuthenticationProvider composite,
            ClientRequestContext requestContext, String expectedMessage) throws IOException {
        Exception thrown = assertThrows(Exception.class, () -> composite.filter(requestContext));
        assertEquals(expectedMessage, thrown.getMessage());
    }

    //endregion

    //region Config mock helpers (OpenAPI spec ID tests)

    private BaseCompositeAuthenticationProvider createProviderWithConfig(String specId, Optional<String> urlValue,
            List<AuthProvider> authProviders) {
        try (MockedStatic<ConfigProvider> configMock = mockStatic(ConfigProvider.class)) {
            Config config = mock(Config.class);
            configMock.when(ConfigProvider::getConfig).thenReturn(config);
            when(config.getOptionalValue(
                    BaseCompositeAuthenticationProvider.REST_CLIENT_URL_CONFIG_PREFIX + specId
                            + BaseCompositeAuthenticationProvider.REST_CLIENT_URL_CONFIG_SUFFIX,
                    String.class))
                    .thenReturn(urlValue);
            return new BaseCompositeAuthenticationProvider(specId, authProviders);
        }
    }

    private BaseCompositeAuthenticationProvider createProviderWithConfig(String specId, Optional<String> urlValue) {
        return createProviderWithConfig(specId, urlValue, List.of());
    }

    private void withConfigMockThrows(String specId, Optional<String> urlValue, ThrowingRunnable action) throws IOException {
        try (MockedStatic<ConfigProvider> configMock = mockStatic(ConfigProvider.class)) {
            Config config = mock(Config.class);
            configMock.when(ConfigProvider::getConfig).thenReturn(config);
            when(config.getOptionalValue(
                    BaseCompositeAuthenticationProvider.REST_CLIENT_URL_CONFIG_PREFIX + specId
                            + BaseCompositeAuthenticationProvider.REST_CLIENT_URL_CONFIG_SUFFIX,
                    String.class))
                    .thenReturn(urlValue);
            action.run();
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws IOException;
    }

    private ClientRequestContext mockRequestContext(String method, String uri) {
        ClientRequestContext ctx = mock(ClientRequestContext.class);
        when(ctx.getMethod()).thenReturn(method);
        when(ctx.getUri()).thenReturn(URI.create(uri));
        lenient().when(ctx.getHeaders()).thenReturn(new MultivaluedHashMap<>());
        return ctx;
    }

    //endregion

    //region Alternative security scheme tests

    @Test
    void testOnlyOneAlternativeProviderIsCalled() throws IOException {
        AuthProvider provider1 = Mockito.mock(AuthProvider.class);
        AuthProvider provider2 = Mockito.mock(AuthProvider.class);

        OperationAuthInfo operation = createOperation();
        when(provider1.operationsToFilter()).thenReturn(List.of(operation));
        lenient().when(provider2.operationsToFilter()).thenReturn(List.of(operation));

        ClientRequestContext requestContext = createRequestContext("POST", "/api/test");
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        givenProviderWillAuthenticate(provider1, headers, "Bearer token1");

        BaseCompositeAuthenticationProvider composite = new BaseCompositeAuthenticationProvider(List.of(provider1, provider2));

        composite.filter(requestContext);

        verify(provider1, times(1)).filter(any(ClientRequestContext.class));
        verify(provider2, never()).filter(any(ClientRequestContext.class));
        assertEquals("Bearer token1", headers.getFirst("Authorization"));
    }

    @Test
    void testBothProvidersAreCalledWhenFirstFails() throws IOException {
        AuthProvider provider1 = Mockito.mock(AuthProvider.class);
        AuthProvider provider2 = Mockito.mock(AuthProvider.class);

        OperationAuthInfo operation = createOperation();
        when(provider1.operationsToFilter()).thenReturn(List.of(operation));
        when(provider2.operationsToFilter()).thenReturn(List.of(operation));

        ClientRequestContext requestContext = createRequestContext("POST", "/api/test");
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        givenProviderWillFail(provider1, new RuntimeException("Missing OAuth2 configuration"));
        givenProviderWillAuthenticate(provider2, headers, "Bearer token2");

        BaseCompositeAuthenticationProvider composite = new BaseCompositeAuthenticationProvider(List.of(provider1, provider2));

        composite.filter(requestContext);

        verify(provider1, times(1)).filter(any(ClientRequestContext.class));
        verify(provider2, times(1)).filter(any(ClientRequestContext.class));
        assertEquals("Bearer token2", headers.getFirst("Authorization"));
    }

    @Test
    void testOnlyRelevantProviderIsCalled() throws IOException {
        AuthProvider provider1 = Mockito.mock(AuthProvider.class);
        AuthProvider provider2 = Mockito.mock(AuthProvider.class);

        OperationAuthInfo operation1 = createOperation();
        OperationAuthInfo operation2 = createOtherOperation();
        when(provider1.operationsToFilter()).thenReturn(List.of(operation1));
        lenient().when(provider2.operationsToFilter()).thenReturn(List.of(operation2));

        ClientRequestContext requestContext = createRequestContext("POST", "/api/test");
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        givenProviderWillAuthenticate(provider1, headers, "Bearer token1");

        BaseCompositeAuthenticationProvider composite = new BaseCompositeAuthenticationProvider(List.of(provider1, provider2));

        composite.filter(requestContext);

        verify(provider1, times(1)).filter(any(ClientRequestContext.class));
        verify(provider2, never()).filter(any(ClientRequestContext.class));
        assertEquals("Bearer token1", headers.getFirst("Authorization"));
    }

    @Test
    void testStopsOnFirstSuccessfulProvider() throws IOException {
        AuthProvider provider1 = Mockito.mock(AuthProvider.class);
        AuthProvider provider2 = Mockito.mock(AuthProvider.class);
        AuthProvider provider3 = Mockito.mock(AuthProvider.class);

        OperationAuthInfo operation = createOperation();
        when(provider1.operationsToFilter()).thenReturn(List.of(operation));
        lenient().when(provider2.operationsToFilter()).thenReturn(List.of(operation));
        lenient().when(provider3.operationsToFilter()).thenReturn(List.of(operation));

        ClientRequestContext requestContext = createRequestContext("POST", "/api/test");
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

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
        AuthProvider provider1 = Mockito.mock(AuthProvider.class);
        AuthProvider provider2 = Mockito.mock(AuthProvider.class);

        OperationAuthInfo operation = createOperation();
        when(provider1.operationsToFilter()).thenReturn(List.of(operation));
        when(provider2.operationsToFilter()).thenReturn(List.of(operation));

        ClientRequestContext requestContext = createRequestContext("POST", "/api/test");

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

    //endregion

    //region OpenAPI spec ID and base URL path tests

    @Test
    void getBaseUrlPathReturnsNullWhenNoSpecId() {
        BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(List.of());
        assertNull(provider.getBaseUrlPath());
    }

    @Test
    void getBaseUrlPathReturnsNullWhenNoConfiguredUrl() {
        BaseCompositeAuthenticationProvider provider = createProviderWithConfig("my_spec_yaml", Optional.empty());
        assertNull(provider.getBaseUrlPath());
    }

    @Test
    void getBaseUrlPathReturnsPathFromConfiguredUrl() {
        BaseCompositeAuthenticationProvider provider = createProviderWithConfig("my_spec_yaml",
                Optional.of("https://example.com/my-apigwstage/v1"));
        assertEquals("/my-apigwstage/v1", provider.getBaseUrlPath());
    }

    @Test
    void getBaseUrlPathReturnsNullForRootPath() {
        BaseCompositeAuthenticationProvider provider = createProviderWithConfig("my_spec_yaml",
                Optional.of("https://example.com"));
        assertNull(provider.getBaseUrlPath());
    }

    @Test
    void getBaseUrlPathReturnsNullForRootPathWithSlash() {
        BaseCompositeAuthenticationProvider provider = createProviderWithConfig("my_spec_yaml",
                Optional.of("https://example.com/"));
        assertNull(provider.getBaseUrlPath());
    }

    @Test
    void getBaseUrlPathStripsTrailingSlash() {
        BaseCompositeAuthenticationProvider provider = createProviderWithConfig("my_spec_yaml",
                Optional.of("https://example.com/api/v1/"));
        assertEquals("/api/v1", provider.getBaseUrlPath());
    }

    @Test
    void canFilterMatchesDirectlyWhenPathsAlign() throws IOException {
        AuthProvider authProvider = mock(AuthProvider.class);
        when(authProvider.operationsToFilter()).thenReturn(List.of(
                OperationAuthInfo.builder().withPath("/data/2.5/weather").withMethod("GET").withId("currentWeatherData")
                        .build()));

        withConfigMockThrows("open_weather_yaml", Optional.of("https://api.openweathermap.org/data/2.5"), () -> {
            BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(
                    "open_weather_yaml", List.of(authProvider));
            ClientRequestContext ctx = mockRequestContext("GET", "https://api.openweathermap.org/data/2.5/weather");
            provider.filter(ctx);
            verify(authProvider).filter(ctx);
        });
    }

    @Test
    void canFilterMatchesByStrippingBaseUrlPathWhenPathsDiffer() throws IOException {
        AuthProvider authProvider = mock(AuthProvider.class);
        when(authProvider.operationsToFilter()).thenReturn(List.of(
                OperationAuthInfo.builder().withPath("/v1/myapp/search").withMethod("GET").withId("searchOp").build()));

        withConfigMockThrows("search_service_yml",
                Optional.of("https://redacted.execute-api.eu-west-1.amazonaws.com/my-apigwstage"), () -> {
                    BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(
                            "search_service_yml", List.of(authProvider));
                    ClientRequestContext ctx = mockRequestContext("GET",
                            "https://redacted.execute-api.eu-west-1.amazonaws.com/my-apigwstage/v1/myapp/search");
                    provider.filter(ctx);
                    verify(authProvider).filter(ctx);
                });
    }

    @Test
    void canFilterMatchesWhenNoBaseUrlPath() throws IOException {
        AuthProvider authProvider = mock(AuthProvider.class);
        when(authProvider.operationsToFilter()).thenReturn(List.of(
                OperationAuthInfo.builder().withPath("/myapp/search").withMethod("GET").withId("searchOp").build()));

        withConfigMockThrows("my_spec_yaml", Optional.of("https://example.com"), () -> {
            BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(
                    "my_spec_yaml", List.of(authProvider));
            ClientRequestContext ctx = mockRequestContext("GET", "https://example.com/myapp/search");
            provider.filter(ctx);
            verify(authProvider).filter(ctx);
        });
    }

    @Test
    void canFilterDoesNotMatchWrongMethod() throws IOException {
        AuthProvider authProvider = mock(AuthProvider.class);
        when(authProvider.operationsToFilter()).thenReturn(List.of(
                OperationAuthInfo.builder().withPath("/myapp/search").withMethod("GET").withId("searchOp").build()));

        withConfigMockThrows("my_spec_yaml", Optional.of("https://example.com"), () -> {
            BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(
                    "my_spec_yaml", List.of(authProvider));
            ClientRequestContext ctx = mockRequestContext("POST", "https://example.com/myapp/search");
            provider.filter(ctx);
            verify(authProvider, never()).filter(any());
        });
    }

    @Test
    void canFilterDoesNotMatchWrongPath() throws IOException {
        AuthProvider authProvider = mock(AuthProvider.class);
        when(authProvider.operationsToFilter()).thenReturn(List.of(
                OperationAuthInfo.builder().withPath("/myapp/search").withMethod("GET").withId("searchOp").build()));

        withConfigMockThrows("my_spec_yaml", Optional.of("https://example.com/api"), () -> {
            BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(
                    "my_spec_yaml", List.of(authProvider));
            ClientRequestContext ctx = mockRequestContext("GET", "https://example.com/api/other/path");
            provider.filter(ctx);
            verify(authProvider, never()).filter(any());
        });
    }

    @Test
    void canFilterWithNoOpenApiSpecIdStillWorks() throws IOException {
        AuthProvider authProvider = mock(AuthProvider.class);
        when(authProvider.operationsToFilter()).thenReturn(List.of(
                OperationAuthInfo.builder().withPath("/myapp/search").withMethod("GET").withId("searchOp").build()));

        BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(List.of(authProvider));
        ClientRequestContext ctx = mockRequestContext("GET", "https://example.com/myapp/search");

        provider.filter(ctx);
        verify(authProvider).filter(ctx);
    }

    @Test
    void backwardCompatibilityLegacyConstructor() {
        BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(List.of());
        assertNull(provider.getBaseUrlPath());
    }

    @Test
    void canFilterDoesNotMatchWhenBaseUrlPathIsPrefixOfDifferentSegment() throws IOException {
        AuthProvider authProvider = mock(AuthProvider.class);
        when(authProvider.operationsToFilter()).thenReturn(List.of(
                OperationAuthInfo.builder().withPath("/users").withMethod("GET").withId("getUsers").build()));

        withConfigMockThrows("my_spec_yaml", Optional.of("https://example.com/api"), () -> {
            BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(
                    "my_spec_yaml", List.of(authProvider));
            ClientRequestContext ctx = mockRequestContext("GET", "https://example.com/apiV2/users");
            provider.filter(ctx);
            verify(authProvider, never()).filter(any());
        });
    }

    @Test
    void canFilterMatchesWhenBaseUrlPathIsExactSegment() throws IOException {
        AuthProvider authProvider = mock(AuthProvider.class);
        when(authProvider.operationsToFilter()).thenReturn(List.of(
                OperationAuthInfo.builder().withPath("/users").withMethod("GET").withId("getUsers").build()));

        withConfigMockThrows("my_spec_yaml", Optional.of("https://example.com/api"), () -> {
            BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(
                    "my_spec_yaml", List.of(authProvider));
            ClientRequestContext ctx = mockRequestContext("GET", "https://example.com/api/users");
            provider.filter(ctx);
            verify(authProvider).filter(ctx);
        });
    }

    @Test
    void canFilterMatchesWhenBaseUrlPathMatchesExactly() throws IOException {
        AuthProvider authProvider = mock(AuthProvider.class);
        when(authProvider.operationsToFilter()).thenReturn(List.of(
                OperationAuthInfo.builder().withPath("/").withMethod("GET").withId("getRoot").build()));

        withConfigMockThrows("my_spec_yaml", Optional.of("https://example.com/api"), () -> {
            BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(
                    "my_spec_yaml", List.of(authProvider));
            ClientRequestContext ctx = mockRequestContext("GET", "https://example.com/api");
            provider.filter(ctx);
            verify(authProvider).filter(ctx);
        });
    }

    @Test
    void canFilterMatchesWithParamInPathAfterStripping() throws IOException {
        AuthProvider authProvider = mock(AuthProvider.class);
        when(authProvider.operationsToFilter()).thenReturn(List.of(
                OperationAuthInfo.builder().withPath("/v1/myapp/{id}").withMethod("GET").withId("getById").build()));

        withConfigMockThrows("my_spec_yaml", Optional.of("https://gateway.example.com/custom-stage"), () -> {
            BaseCompositeAuthenticationProvider provider = new BaseCompositeAuthenticationProvider(
                    "my_spec_yaml", List.of(authProvider));
            ClientRequestContext ctx = mockRequestContext("GET", "https://gateway.example.com/custom-stage/v1/myapp/123");
            provider.filter(ctx);
            verify(authProvider).filter(ctx);
        });
    }

    //endregion
}
