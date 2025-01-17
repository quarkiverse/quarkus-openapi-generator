package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Provides bearer token authentication or any other valid scheme.
 *
 * @see <a href="https://swagger.io/docs/specification/authentication/bearer-authentication/">Bearer Authentication</a>
 */
public class BearerAuthenticationProvider extends AbstractAuthProvider {

    static final String BEARER_TOKEN = "bearer-token";

    private final String scheme;

    public BearerAuthenticationProvider(final String openApiSpecId, final String name, final String scheme,
            List<OperationAuthInfo> operations) {
        super(name, openApiSpecId, operations);
        this.scheme = scheme;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String bearerToken;
        if (isTokenPropagation()) {
            bearerToken = getTokenForPropagation(requestContext.getHeaders());
            bearerToken = sanitizeBearerToken(bearerToken);
        } else {
            bearerToken = getBearerToken();
        }
        if (!bearerToken.isBlank()) {
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION,
                    AuthUtils.authTokenOrBearer(this.scheme, bearerToken));
        }
    }

    private String getBearerToken() {
        return ConfigProvider.getConfig().getOptionalValue(getCanonicalAuthConfigPropertyName(BEARER_TOKEN), String.class)
                .orElse("");
    }
}
