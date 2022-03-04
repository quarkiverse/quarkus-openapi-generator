package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.UriBuilder;

/**
 * Provider for API Key authentication.
 */
public class ApiKeyAuthenticationProvider implements ClientRequestFilter {

    private final String name;
    private final ApiKeyIn apiKeyIn;
    private final String apiKeyName;
    private final AuthProvidersConfig authProvidersConfig;

    public ApiKeyAuthenticationProvider(final String name, final ApiKeyIn apiKeyIn, final String apiKeyName,
            final AuthProvidersConfig authProvidersConfig) {
        this.apiKeyIn = apiKeyIn;
        this.name = name;
        this.apiKeyName = apiKeyName;
        this.authProvidersConfig = authProvidersConfig;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        switch (apiKeyIn) {
            case query:
                requestContext.setUri(UriBuilder.fromUri(requestContext.getUri()).queryParam(apiKeyName, getApiKey()).build());
                break;
            case cookie:
                requestContext.getCookies().put(apiKeyName, new Cookie(apiKeyName, getApiKey()));
                break;
            case header:
                requestContext.getHeaders().add(apiKeyName, getApiKey());
                break;
        }
    }

    private String getApiKey() {
        return this.authProvidersConfig.auth().getOrDefault(name + "/api-key", "");
    }
}
