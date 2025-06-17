package io.quarkiverse.openapi.generator.oidc.providers;

import static io.quarkiverse.openapi.generator.AuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.providers.AbstractAuthProvider;
import io.quarkiverse.openapi.generator.providers.AuthUtils;
import io.quarkiverse.openapi.generator.providers.CredentialsContext;
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
        String bearerToken = "";

        if (this.isTokenPropagation()) {
            bearerToken = this.getTokenForPropagation(requestContext.getHeaders());
        } else {
            Optional<String> optionalBearerToken = this.getCredentialsProvider()
                    .getOauth2BearerToken(CredentialsContext.builder()
                            .requestContext(requestContext)
                            .openApiSpecId(getOpenApiSpecId())
                            .authName(getName())
                            .build());
            if (optionalBearerToken.isPresent()) {
                bearerToken = optionalBearerToken.get();
            } else {
                delegate.filter(requestContext);
            }
        }

        if (!isEmptyOrBlank(bearerToken)) {
            addAuthorizationHeader(requestContext.getHeaders(),
                    AuthUtils.authTokenOrBearer("Bearer", AbstractAuthProvider.sanitizeBearerToken(bearerToken)));
        } else {
            LOGGER.debug("Token propagation is not enabled or {} configured propagation header not populated and there is" +
                    " no custom credential provider implementation. The {} header will be set using delegate's filter. ",
                    getHeaderForPropagation(getOpenApiSpecId(), getName()), HttpHeaders.AUTHORIZATION);
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
