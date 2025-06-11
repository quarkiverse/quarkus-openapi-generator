package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

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

        if (!bearerToken.isBlank()) {
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, AuthUtils.authTokenOrBearer(this.scheme, bearerToken));
        }
        LOGGER.debug("Header keys set in filtered requestContext: {}", requestContext.getHeaders().keySet());
    }

    private String getBearerToken(ClientRequestContext requestContext) {
        return credentialsProvider.getBearerToken(CredentialsContext.builder()
                .requestContext(requestContext)
                .openApiSpecId(getOpenApiSpecId())
                .authName(getName())
                .build());
    }
}
