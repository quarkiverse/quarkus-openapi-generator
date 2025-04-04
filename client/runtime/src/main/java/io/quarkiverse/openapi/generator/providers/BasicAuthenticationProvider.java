package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for Basic Authentication.
 * Username and password should be read by generated configuration properties, which is only known after openapi spec processing
 * during build time.
 */
public class BasicAuthenticationProvider extends AbstractAuthProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticationProvider.class);

    static final String USER_NAME = "username";
    static final String PASSWORD = "password";

    public BasicAuthenticationProvider(final String openApiSpecId, String name, List<OperationAuthInfo> operations) {
        super(name, openApiSpecId, operations);
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
        String basicToken = AuthUtils.basicAuthAccessTokenWithoutPrefix(getUsername(), getPassword());

        if (isTokenPropagation()) {
            LOGGER.warn("Token propagation enabled for BasicAuthentication");
            basicToken = sanitizeBasicToken(getTokenForPropagation(requestContext.getHeaders()));
        }

        if (!basicToken.isBlank()) {
            requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION,
                    AuthUtils.basicAuthAccessToken(basicToken));
        }

    }
}
