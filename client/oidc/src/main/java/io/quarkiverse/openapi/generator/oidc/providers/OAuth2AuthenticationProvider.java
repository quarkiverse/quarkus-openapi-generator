package io.quarkiverse.openapi.generator.oidc.providers;

import static io.quarkiverse.openapi.generator.AuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.providers.AbstractAuthProvider;
import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;
import io.quarkiverse.openapi.generator.providers.OperationAuthInfo;
import io.quarkus.oidc.common.runtime.OidcConstants;

public class OAuth2AuthenticationProvider extends AbstractAuthProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthenticationProvider.class);

    private final OidcClientRequestFilterDelegate delegate;

    public OAuth2AuthenticationProvider(String name,
            String openApiSpecId, OidcClientRequestFilterDelegate delegate, List<OperationAuthInfo> operations) {
        super(name, openApiSpecId, operations, new ConfigCredentialsProvider());
        this.delegate = delegate;
        validateConfig();
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (isTokenPropagation()) {
            String bearerToken = sanitizeBearerToken(getTokenForPropagation(requestContext.getHeaders()));
            if (!isEmptyOrBlank(bearerToken)) {
                addAuthorizationHeader(requestContext.getHeaders(), OidcConstants.BEARER_SCHEME + " " + bearerToken);
            } else {
                LOGGER.debug("No oauth2 bearer token was found to propagate for the security scheme: {}." +
                        " You must verify that the request header: {} is set.", getName(), getHeaderForPropagation());
            }
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
