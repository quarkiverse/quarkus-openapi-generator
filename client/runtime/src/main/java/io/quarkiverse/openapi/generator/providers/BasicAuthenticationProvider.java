package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.AuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkiverse.openapi.generator.OpenApiGeneratorException;

/**
 * Provider for Basic Authentication.
 * Username and password should be read by generated configuration properties, which is only known after openapi spec processing
 * during build time.
 */
public class BasicAuthenticationProvider extends AbstractAuthProvider {

    static final String USER_NAME = "username";
    static final String PASSWORD = "password";

    public BasicAuthenticationProvider(final String openApiSpecId, String name, List<OperationAuthInfo> operations) {
        super(name, openApiSpecId, operations);
        validateConfig();
    }

    private String getUsername() {
        return ConfigProvider.getConfig().getOptionalValue(getCanonicalAuthConfigPropertyName(USER_NAME), String.class)
                .orElse("");
    }

    private String getPassword() {
        return ConfigProvider.getConfig().getOptionalValue(getCanonicalAuthConfigPropertyName(PASSWORD), String.class)
                .orElse("");
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION,
                AuthUtils.basicAuthAccessToken(getUsername(), getPassword()));
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
