package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

/**
 * Provides bearer token authentication or any other valid scheme.
 *
 * @see <a href="https://swagger.io/docs/specification/authentication/bearer-authentication/">Bearer Authentication</a>
 */
public class BearerAuthenticationProvider implements ClientRequestFilter {

    private final String name;
    private final String scheme;
    private final AuthProvidersConfig authProvidersConfig;

    public BearerAuthenticationProvider(final String name, final String scheme, final AuthProvidersConfig authProvidersConfig) {
        this.authProvidersConfig = authProvidersConfig;
        this.name = name;
        this.scheme = scheme;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION,
                AuthUtils.authTokenOrBearer(this.scheme, this.getBearerToken()));
    }

    private String getBearerToken() {
        return this.authProvidersConfig.auth().getOrDefault(name + "/bearer-token", "");
    }
}
