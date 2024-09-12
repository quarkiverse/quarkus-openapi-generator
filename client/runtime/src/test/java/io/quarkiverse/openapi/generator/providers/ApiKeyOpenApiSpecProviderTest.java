package io.quarkiverse.openapi.generator.providers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.jboss.resteasy.specimpl.MultivaluedTreeMap;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import io.quarkiverse.openapi.generator.AuthConfig;

class ApiKeyOpenApiSpecProviderTest extends AbstractOpenApiSpecProviderTest<ApiKeyAuthenticationProvider> {

    private static final String API_KEY_NAME = "API_KEY_NAME";
    private static final String API_KEY_VALUE = "API_KEY_VALUE";
    private static final String API_KEY_AUTH_HEADER_VALUE = "API_KEY_AUTH_HEADER_VALUE";

    private static final URI INVOKED_URI = URI.create("https://example.com/my-service");

    @Captor
    private ArgumentCaptor<URI> uriCaptor;

    @Override
    protected void createConfiguration() {
        super.createConfiguration();
        authConfig.authConfigParams.put(ApiKeyAuthenticationProvider.API_KEY, API_KEY_VALUE);
    }

    @Override
    protected ApiKeyAuthenticationProvider createProvider(String openApiSpecId, String authSchemeName,
            AuthConfig authConfig) {
        return new ApiKeyAuthenticationProvider(openApiSpecId, authSchemeName, ApiKeyIn.header, API_KEY_NAME,
                authConfig, List.of());
    }

    @Test
    void filterHeaderFromAuthorizationHeaderDefaultCase() throws IOException {
        doReturn(API_KEY_AUTH_HEADER_VALUE).when(requestContext).getHeaderString("Authorization");
        provider.filter(requestContext);
        assertHeader(headers, API_KEY_NAME, API_KEY_AUTH_HEADER_VALUE);
    }

    @Test
    void filterHeaderFromAuthorizationHeaderCase() throws IOException {
        authConfig.authConfigParams.put(ApiKeyAuthenticationProvider.USE_AUTHORIZATION_HEADER_VALUE, "true");
        doReturn(API_KEY_AUTH_HEADER_VALUE).when(requestContext).getHeaderString("Authorization");
        provider.filter(requestContext);
        assertHeader(headers, API_KEY_NAME, API_KEY_AUTH_HEADER_VALUE);
        authConfig.authConfigParams.remove(ApiKeyAuthenticationProvider.USE_AUTHORIZATION_HEADER_VALUE);
    }

    @Test
    void filterHeaderNotFromAuthorizationHeaderCase() throws IOException {
        authConfig.authConfigParams.put(ApiKeyAuthenticationProvider.USE_AUTHORIZATION_HEADER_VALUE, "false");
        doReturn(API_KEY_AUTH_HEADER_VALUE).when(requestContext).getHeaderString("Authorization");
        provider.filter(requestContext);
        assertHeader(headers, API_KEY_NAME, API_KEY_VALUE);
        authConfig.authConfigParams.remove(ApiKeyAuthenticationProvider.USE_AUTHORIZATION_HEADER_VALUE);
    }

    @Test
    void filterHeaderCase() throws IOException {
        provider.filter(requestContext);
        assertHeader(headers, API_KEY_NAME, API_KEY_VALUE);
    }

    @Test
    void filterQueryCase() throws IOException {
        doReturn(INVOKED_URI).when(requestContext).getUri();
        provider = new ApiKeyAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, ApiKeyIn.query, API_KEY_NAME,
                authConfig, List.of());
        provider.filter(requestContext);
        verify(requestContext).setUri(uriCaptor.capture());
        assertThat(uriCaptor.getValue())
                .isNotNull()
                .hasParameter(API_KEY_NAME, API_KEY_VALUE);
    }

    @Test
    void filterCookieCaseEmpty() throws IOException {
        final MultivaluedMap<String, Object> headers = new MultivaluedTreeMap<>();
        doReturn(headers).when(requestContext).getHeaders();
        provider = new ApiKeyAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, ApiKeyIn.cookie, API_KEY_NAME,
                authConfig, List.of());
        provider.filter(requestContext);
        final List<Object> cookies = headers.get(HttpHeaders.COOKIE);
        assertThat(cookies)
                .singleElement()
                .satisfies(cookie -> assertCookie(cookie, API_KEY_NAME, API_KEY_VALUE));
    }

    @Test
    void filterCookieCaseExisting() throws IOException {
        final MultivaluedMap<String, Object> headers = new MultivaluedTreeMap<>();
        final String existingCookieName = "quarkus";
        final String existingCookieValue = "rocks";
        final Cookie existingCookie = new Cookie.Builder(existingCookieName)
                .value(existingCookieValue)
                .build();
        headers.add(HttpHeaders.COOKIE, existingCookie);
        doReturn(headers).when(requestContext).getHeaders();
        provider = new ApiKeyAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, ApiKeyIn.cookie, API_KEY_NAME,
                authConfig, List.of());
        provider.filter(requestContext);
        final List<Object> cookies = headers.get(HttpHeaders.COOKIE);
        assertThat(cookies)
                .satisfiesExactlyInAnyOrder(
                        cookie -> assertCookie(cookie, existingCookieName, existingCookieValue),
                        cookie -> assertCookie(cookie, API_KEY_NAME, API_KEY_VALUE));
    }

    @Test
    void tokenPropagationNotSupported() {
        authConfig.tokenPropagation = Optional.of(true);
        assertThatThrownBy(() -> new ApiKeyAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, ApiKeyIn.header,
                API_KEY_NAME, authConfig, List.of()))
                .hasMessageContaining("quarkus.openapi-generator.%s.auth.%s.token-propagation", OPEN_API_FILE_SPEC_ID,
                        AUTH_SCHEME_NAME);
    }

    private void assertCookie(final Object cookie, final String name, final String value) {
        assertThat(cookie)
                .asInstanceOf(InstanceOfAssertFactories.type(Cookie.class))
                .matches(c -> Objects.equals(c.getName(), name))
                .matches(c -> Objects.equals(c.getValue(), value));
    }
}
