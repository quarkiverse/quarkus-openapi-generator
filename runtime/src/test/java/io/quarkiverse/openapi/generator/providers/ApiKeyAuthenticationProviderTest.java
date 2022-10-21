package io.quarkiverse.openapi.generator.providers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.Cookie;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;

class ApiKeyAuthenticationProviderTest extends AbstractAuthenticationProviderTest<ApiKeyAuthenticationProvider> {

    private static final String API_KEY_NAME = "API_KEY_NAME";
    private static final String API_KEY_VALUE = "API_KEY_VALUE";

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
            OpenApiGeneratorConfig openApiGeneratorConfig) {
        return new ApiKeyAuthenticationProvider(openApiSpecId, authSchemeName, ApiKeyIn.header, API_KEY_NAME,
                openApiGeneratorConfig);
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
                generatorConfig);
        provider.filter(requestContext);
        verify(requestContext).setUri(uriCaptor.capture());
        assertThat(uriCaptor.getValue())
                .isNotNull()
                .hasParameter(API_KEY_NAME, API_KEY_VALUE);
    }

    @Test
    void filterCookieCase() throws IOException {
        Map<String, Cookie> cookies = new HashMap<>();
        doReturn(cookies).when(requestContext).getCookies();
        provider = new ApiKeyAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, ApiKeyIn.cookie, API_KEY_NAME,
                generatorConfig);
        provider.filter(requestContext);
        Cookie cookie = cookies.get(API_KEY_NAME);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo(API_KEY_NAME);
        assertThat(cookie.getValue()).isEqualTo(API_KEY_VALUE);
    }

    @Test
    void tokenPropagationNotSupported() {
        authConfig.tokenPropagation = Optional.of(true);
        assertThatThrownBy(() -> new ApiKeyAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, ApiKeyIn.header,
                API_KEY_NAME, generatorConfig))
                .hasMessageContaining("quarkus.openapi-generator.%s.auth.%s.token-propagation", OPEN_API_FILE_SPEC_ID,
                        AUTH_SCHEME_NAME);
    }
}
