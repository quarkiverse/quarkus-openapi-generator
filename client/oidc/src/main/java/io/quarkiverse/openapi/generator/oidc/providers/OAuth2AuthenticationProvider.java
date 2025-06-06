package io.quarkiverse.openapi.generator.oidc.providers;

import static io.quarkiverse.openapi.generator.AuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.providers.AbstractAuthProvider;
import io.quarkiverse.openapi.generator.providers.AuthUtils;
import io.quarkiverse.openapi.generator.providers.CredentialsProvider;
import io.quarkiverse.openapi.generator.providers.OperationAuthInfo;

public class OAuth2AuthenticationProvider extends AbstractAuthProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthenticationProvider.class);

    private final OidcClientRequestFilterDelegate delegate;

    public OAuth2AuthenticationProvider(String name,
            String openApiSpecId, OidcClientRequestFilterDelegate delegate, List<OperationAuthInfo> operations,
            CredentialsProvider credentialsProvider) {
        super(name, openApiSpecId, operations, credentialsProvider);
        this.delegate = delegate;
        validateConfig();
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String bearerToken;

        if (this.isTokenPropagation()) {
            bearerToken = this.getTokenForPropagation(requestContext.getHeaders());
        } else {
            delegate.filter(requestContext);
            bearerToken = this.getCredentialsProvider().getOauth2BearerToken(CredentialsProvider.CredentialsContext.builder()
                    .requestContext(requestContext)
                    .openApiSpecId(getOpenApiSpecId())
                    .authName(getName())
                    .build());
        }

        if (bearerToken != null && !bearerToken.isBlank()) {
            requestContext.getHeaders().remove(HttpHeaders.AUTHORIZATION);
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION,
                    AuthUtils.authTokenOrBearer("Bearer", AbstractAuthProvider.sanitizeBearerToken(bearerToken)));
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
