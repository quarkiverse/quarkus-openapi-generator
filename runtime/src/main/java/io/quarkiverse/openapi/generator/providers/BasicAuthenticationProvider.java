package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.SpecItemAuthConfig.TOKEN_PROPAGATION;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.HttpHeaders;

import io.quarkiverse.openapi.generator.CodegenConfig;
import io.quarkiverse.openapi.generator.CodegenException;

/**
 * Provider for Basic Authentication.
 * Username and password should be read by generated configuration properties, which is only known after openapi spec processing
 * during build time.
 */
public class BasicAuthenticationProvider extends AbstractAuthProvider {

    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";

    public BasicAuthenticationProvider(final String openApiSpecId, String name, final CodegenConfig codegenConfig) {
        super(openApiSpecId, name, codegenConfig);
        validateConfig();
    }

    private String getUsername() {
        return getAuthConfigParam(USER_NAME, "");
    }

    private String getPassword() {
        return getAuthConfigParam(PASSWORD, "");
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION,
                AuthUtils.basicAuthAccessToken(getUsername(), getPassword()));
    }

    private void validateConfig() {
        if (isTokenPropagation()) {
            throw new CodegenException(
                    "Token propagation is not admitted for the OpenApi securitySchemes of \"type\": \"http\", \"scheme\": \"basic\"."
                            +
                            " A potential source of the problem might be that the configuration property " +
                            getCanonicalAuthConfigPropertyName(TOKEN_PROPAGATION) +
                            " was set with the value true in your application, please check your configuration.");
        }
    }
}
