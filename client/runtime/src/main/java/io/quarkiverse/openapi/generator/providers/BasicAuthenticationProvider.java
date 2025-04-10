package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.AuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import io.quarkiverse.openapi.generator.OpenApiGeneratorException;

/**
 * Provider for Basic Authentication.
 * Username and password should be read by generated configuration properties, which is only known after openapi spec processing
 * during build time.
 */
public class BasicAuthenticationProvider extends AbstractAuthProvider {

    public BasicAuthenticationProvider(final String openApiSpecId, String name, List<OperationAuthInfo> operations,
            CredentialsProvider credentialsProvider) {
        super(name, openApiSpecId, operations, credentialsProvider);
        validateConfig();
    }

    public BasicAuthenticationProvider(final String openApiSpecId, String name, List<OperationAuthInfo> operations) {
        this(openApiSpecId, name, operations, new ConfigCredentialsProvider());
    }

    private String getUsername(ClientRequestContext requestContext) {
        return credentialsProvider.getBasicUsername(requestContext, getOpenApiSpecId(), getName());
    }

    private String getPassword(ClientRequestContext requestContext) {
        return credentialsProvider.getBasicPassword(requestContext, getOpenApiSpecId(), getName());
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION,
                AuthUtils.basicAuthAccessToken(getUsername(requestContext), getPassword(requestContext)));
    }

    private void validateConfig() {
        if (isTokenPropagation()) {
            throw new OpenApiGeneratorException(
                    "Token propagation is not admitted for the OpenApi securitySchemes of \"type\": \"http\", \"scheme\": \"basic\"."
                            +
                            " A potential source of the problem might be that the configuration property " +
                            getCanonicalAuthConfigPropertyName(TOKEN_PROPAGATION) +
                            " was set with the value true in your application, please check your configuration.");
        }
    }
}
