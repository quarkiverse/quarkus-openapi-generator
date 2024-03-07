package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;

/**
 * Provides bearer token authentication or any other valid scheme.
 *
 * @see <a href="https://swagger.io/docs/specification/authentication/bearer-authentication/">Bearer Authentication</a>
 */
public class BearerAuthenticationProvider extends AbstractAuthProvider {

    static final String BEARER_TOKEN = "bearer-token";

    private final String scheme;

    public BearerAuthenticationProvider(final String openApiSpecId, final String name, final String scheme,
            final OpenApiGeneratorConfig generatorConfig) {
        super(generatorConfig);
        init(name, openApiSpecId);
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
        return getAuthConfigParam(BEARER_TOKEN, "");
    }
}
