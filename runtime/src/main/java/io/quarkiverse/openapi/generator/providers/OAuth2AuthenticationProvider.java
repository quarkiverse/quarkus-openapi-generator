package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.AuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkus.oidc.common.runtime.OidcConstants;

@jakarta.annotation.Priority(jakarta.ws.rs.Priorities.AUTHENTICATION)
@jakarta.enterprise.context.Dependent
public class OAuth2AuthenticationProvider extends AbstractAuthProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthenticationProvider.class);

    private OidcClientRequestFilterDelegate delegate;

    @SuppressWarnings("unused")
    OAuth2AuthenticationProvider() {
        // Required by CDI. Not supposed to be used.
        delegate = null;
    }

    @jakarta.inject.Inject
    public OAuth2AuthenticationProvider(final OpenApiGeneratorConfig generatorConfig) {
        super(generatorConfig);
    }

    public void init(String name, String openApiSpecId, OidcClientRequestFilterDelegate delegate) {
        this.delegate = delegate;
        super.init(name, openApiSpecId);
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
