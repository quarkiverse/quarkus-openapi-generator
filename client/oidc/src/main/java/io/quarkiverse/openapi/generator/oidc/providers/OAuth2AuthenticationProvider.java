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
            if (isEmptyOrBlank(bearerToken)) {
                LOGGER.debug(
                        "Token propagation for OAUTH2 is enabled but the configured propagation header defined by {} is not present",
                        getHeaderForPropagation(getOpenApiSpecId(), getName()));
            }
        } else {
            Optional<String> optionalBearerToken = this.getCredentialsProvider()
                    .getOauth2BearerToken(CredentialsContext.builder()
                            .requestContext(requestContext)
                            .openApiSpecId(getOpenApiSpecId())
                            .authName(getName())
                            .build());
            if (optionalBearerToken.isPresent()) {
                bearerToken = optionalBearerToken.get();
                if (isEmptyOrBlank(bearerToken)) {
                    LOGGER.debug("The CredentialProvider implementation returned an empty OAUTH2 bearer");
                }
            } else {
                LOGGER.debug(
                        "There is no custom CredentialProvider implementation, the {} header will be set using delegate's filter. ",
                        HttpHeaders.AUTHORIZATION);
                delegate.filter(requestContext);
            }
        }

        if (!isEmptyOrBlank(bearerToken)) {
            addAuthorizationHeader(requestContext.getHeaders(),
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
