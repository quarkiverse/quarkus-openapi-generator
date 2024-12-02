package io.quarkiverse.openapi.generator.oidc.providers;

import static io.quarkiverse.openapi.generator.AuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.AuthConfig;
import io.quarkiverse.openapi.generator.providers.AbstractAuthProvider;
import io.quarkiverse.openapi.generator.providers.OperationAuthInfo;
import io.quarkus.oidc.common.runtime.OidcConstants;

public class OAuth2AuthenticationProvider extends AbstractAuthProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthenticationProvider.class);

    private final OidcClientRequestFilterDelegate delegate;

    public OAuth2AuthenticationProvider(final AuthConfig authConfig, String name,
            String openApiSpecId, OidcClientRequestFilterDelegate delegate, List<OperationAuthInfo> operations) {
        super(authConfig, name, openApiSpecId, operations);
        this.delegate = delegate;
        validateConfig();
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (isTokenPropagation()) {
            String bearerToken = getTokenForPropagation(requestContext.getHeaders());
            bearerToken = sanitizeBearerToken(bearerToken);
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, OidcConstants.BEARER_SCHEME + " " + bearerToken);
        } else {
            delegate.filter(requestContext);
        }
    }

    private void validateConfig() {
        if (isTokenPropagation()) {
            LOGGER.warn("Token propagation was enabled for a the oauth2: {} securityScheme in the specification file: {}. " +
                    "This configuration can be done by using the property: {} and is not necessary a problem if the configuration is intentional.",
                    getName(), getOpenApiSpecId(), getCanonicalAuthConfigPropertyName(TOKEN_PROPAGATION));
        }
    }

    public interface OidcClientRequestFilterDelegate {
        void filter(ClientRequestContext requestContext) throws IOException;
    }
}
