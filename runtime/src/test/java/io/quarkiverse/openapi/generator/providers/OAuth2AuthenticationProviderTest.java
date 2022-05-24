package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderName;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Optional;

import javax.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkus.oidc.client.filter.OidcClientRequestFilter;

class OAuth2AuthenticationProviderTest extends AbstractAuthenticationProviderTest<OAuth2AuthenticationProvider> {

    private static final String OIDC_TOKEN = "OIDC_TOKEN";
    private static final String HEADER_NAME = "HEADER_NAME";

    @Override
    protected OAuth2AuthenticationProvider createProvider(String openApiSpecId, String authSchemeName,
            OpenApiGeneratorConfig openApiGeneratorConfig) {
        return new OAuth2AuthenticationProviderMock(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, openApiGeneratorConfig);
    }

    @Test
    void filter() throws IOException {
        OidcClientRequestFilter oidcClientRequestFilter = ((OAuth2AuthenticationProviderMock) provider).getDelegate();
        provider.filter(requestContext);
        verify(oidcClientRequestFilter).filter(requestContext);
    }

    @Test
    void filterWithPropagationByDefault() throws IOException {
        authConfig.tokenPropagation = Optional.of(true);
        headers.putSingle(propagationHeaderName(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME,
                HttpHeaders.AUTHORIZATION), OIDC_TOKEN);
        provider.filter(requestContext);
        assertHeader(headers, HttpHeaders.AUTHORIZATION, "Bearer " + OIDC_TOKEN);
    }

    @Test
    void filterWithPropagationWithTokenName() throws IOException {
        authConfig.tokenPropagation = Optional.of(true);
        authConfig.headerName = Optional.of(HEADER_NAME);
        headers.putSingle(
                propagationHeaderName(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, HEADER_NAME),
                OIDC_TOKEN);
        provider.filter(requestContext);
        assertHeader(headers, HttpHeaders.AUTHORIZATION, "Bearer " + OIDC_TOKEN);
    }

    private static class OidcClientRequestFilterDelegateMock
            extends OAuth2AuthenticationProvider.OidcClientRequestFilterDelegate {

        public OidcClientRequestFilterDelegateMock(String clientId) {
            super(clientId);
        }

        @Override
        public void init() {
            //do nothing, we are just mocking, and we don't want super.init() to be executed.
        }
    }

    private static class OAuth2AuthenticationProviderMock extends OAuth2AuthenticationProvider {

        private OidcClientRequestFilterDelegateMock delegate;

        public OAuth2AuthenticationProviderMock(String openApiSpecId, String name, OpenApiGeneratorConfig generatorConfig) {
            super(openApiSpecId, name, generatorConfig);
        }

        @Override
        OAuth2AuthenticationProvider.OidcClientRequestFilterDelegate createDelegate() {
            this.delegate = spy(new OidcClientRequestFilterDelegateMock(null));
            return delegate;
        }

        public OidcClientRequestFilterDelegateMock getDelegate() {
            return delegate;
        }
    }

}
