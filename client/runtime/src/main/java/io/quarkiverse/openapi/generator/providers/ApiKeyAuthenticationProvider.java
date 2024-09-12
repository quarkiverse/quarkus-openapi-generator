package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.AuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.AuthConfig;
import io.quarkiverse.openapi.generator.OpenApiGeneratorException;

/**
 * Provider for API Key authentication.
 */
public class ApiKeyAuthenticationProvider extends AbstractAuthProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyAuthenticationProvider.class);

    static final String API_KEY = "api-key";
    static final String USE_AUTHORIZATION_HEADER_VALUE = "use-authorization-header-value";

    private final ApiKeyIn apiKeyIn;
    private final String apiKeyName;

    public ApiKeyAuthenticationProvider(final String openApiSpecId, final String name, final ApiKeyIn apiKeyIn,
            final String apiKeyName,
            final AuthConfig authConfig, List<OperationAuthInfo> operations) {
        super(authConfig, name, openApiSpecId, operations);
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
                requestContext.getHeaders().add(HttpHeaders.COOKIE, new Cookie.Builder(apiKeyName).value(getApiKey()).build());
                break;
            case header:
                if (requestContext.getHeaderString("Authorization") != null
                        && !requestContext.getHeaderString("Authorization").isEmpty()
                        && isUseAuthorizationHeaderValue()) {
                    requestContext.getHeaders().putSingle(apiKeyName, requestContext.getHeaderString("Authorization"));
                } else
                    requestContext.getHeaders().putSingle(apiKeyName, getApiKey());
                break;
        }
    }

    private String getApiKey() {
        final String key = getAuthConfigParam(API_KEY, "");
        if (key.isEmpty()) {
            LOGGER.warn("configured " + API_KEY + " property (see application.properties) is empty. hint: configure it.");
        }
        return key;
    }

    private boolean isUseAuthorizationHeaderValue() {
        final String value = getAuthConfigParam(USE_AUTHORIZATION_HEADER_VALUE, "true");
        return "true".equals(value);
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
