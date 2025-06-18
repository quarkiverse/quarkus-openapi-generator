package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider.BEARER_TOKEN;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides bearer token authentication or any other valid scheme.
 *
 * @see <a href="https://swagger.io/docs/specification/authentication/bearer-authentication/">Bearer Authentication</a>
 */
public class BearerAuthenticationProvider extends AbstractAuthProvider {

    private final String scheme;
    private static final Logger LOGGER = LoggerFactory.getLogger(BearerAuthenticationProvider.class);

    public BearerAuthenticationProvider(final String openApiSpecId, final String name, final String scheme,
            List<OperationAuthInfo> operations, CredentialsProvider credentialsProvider) {
        super(name, openApiSpecId, operations, credentialsProvider);
        this.scheme = scheme;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        LOGGER.debug("Headers keys set in incoming requestContext: {}", requestContext.getHeaders().keySet());
        String bearerToken = getBearerToken(requestContext);

        if (isTokenPropagation()) {
            bearerToken = sanitizeBearerToken(getTokenForPropagation(requestContext.getHeaders()));
        }

        if (!isEmptyOrBlank(bearerToken)) {
            addAuthorizationHeader(requestContext.getHeaders(), AuthUtils.authTokenOrBearer(this.scheme, bearerToken));
        } else {
            LOGGER.debug("No bearer token was found for the security scheme: {}." +
                    " You must verify that the property: {} is properly configured, or the request header: {} is set when the token propagation is enabled.",
                    getName(), getCanonicalAuthConfigPropertyName(BEARER_TOKEN, getOpenApiSpecId(), getName()),
                    getHeaderForPropagation(getOpenApiSpecId(), getName()));
        }
        LOGGER.debug("Header keys set in filtered requestContext: {}", requestContext.getHeaders().keySet());
    }

    private String getBearerToken(ClientRequestContext requestContext) {
        return credentialsProvider.getBearerToken(CredentialsContext.builder()
                .requestContext(requestContext)
                .openApiSpecId(getOpenApiSpecId())
                .authName(getName())
                .build()).orElse("");
    }
}
