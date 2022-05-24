package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.AuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.UriBuilder;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkiverse.openapi.generator.OpenApiGeneratorException;

/**
 * Provider for API Key authentication.
 */
public class ApiKeyAuthenticationProvider extends AbstractAuthProvider {

    static final String API_KEY = "api-key";

    private final ApiKeyIn apiKeyIn;
    private final String apiKeyName;

    public ApiKeyAuthenticationProvider(final String openApiSpecId, final String name, final ApiKeyIn apiKeyIn,
            final String apiKeyName,
            final OpenApiGeneratorConfig generatorConfig) {
        super(openApiSpecId, name, generatorConfig);
        this.apiKeyIn = apiKeyIn;
        this.apiKeyName = apiKeyName;
        validateConfig();
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
        return getAuthConfigParam(API_KEY, "");
    }

    private void validateConfig() {
        if (isTokenPropagation()) {
            throw new OpenApiGeneratorException(
                    "Token propagation is not admitted for the OpenApi securitySchemes of \"type\": \"apiKey\"." +
                            " A potential source of the problem might be that the configuration property "
                            + getCanonicalAuthConfigPropertyName(TOKEN_PROPAGATION) +
                            " was set with the value true in your application, please check your configuration.");
        }
    }
}
