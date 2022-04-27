package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.UriBuilder;

/**
 * Provider for API Key authentication.
 */
public class ApiKeyAuthenticationProvider implements AuthProvider {

    private final String name;
    private final ApiKeyIn apiKeyIn;
    private final String apiKeyName;
    private final AuthProvidersConfig authProvidersConfig;
    private final List<OperationAuthInfo> applyToOperations = new ArrayList<>();

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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<OperationAuthInfo> operationsToFilter() {
        return applyToOperations;
    }

    @Override
    public AuthProvider addOperation(OperationAuthInfo operationAuthInfo) {
        this.applyToOperations.add(operationAuthInfo);
        return this;
    }
}
