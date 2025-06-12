package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider.PASSWORD;
import static io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider.USER_NAME;

import java.io.IOException;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for Basic Authentication.
 * Username and password should be read by generated configuration properties, which is only known after openapi spec processing
 * during build time.
 */
public class BasicAuthenticationProvider extends AbstractAuthProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticationProvider.class);

    public BasicAuthenticationProvider(final String openApiSpecId, String name, List<OperationAuthInfo> operations,
            CredentialsProvider credentialsProvider) {
        super(name, openApiSpecId, operations, credentialsProvider);
    }

    private String getUsername(ClientRequestContext requestContext) {
        return credentialsProvider.getBasicUsername(CredentialsContext.builder()
                .requestContext(requestContext)
                .openApiSpecId(getOpenApiSpecId())
                .authName(getName())
                .build());
    }

    private String getPassword(ClientRequestContext requestContext) {
        return credentialsProvider.getBasicPassword(CredentialsContext.builder()
                .requestContext(requestContext)
                .openApiSpecId(getOpenApiSpecId())
                .authName(getName())
                .build());
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String basicToken = AuthUtils.basicAuthAccessTokenWithoutPrefix(getUsername(requestContext),
                getPassword(requestContext));

        if (isTokenPropagation()) {
            LOGGER.warn("Token propagation enabled for BasicAuthentication");
            basicToken = sanitizeBasicToken(getTokenForPropagation(requestContext.getHeaders()));
        }

        if (!isEmptyOrBlank(basicToken)) {
            addAuthorizationHeader(requestContext.getHeaders(), AuthUtils.basicAuthAccessToken(basicToken));
        } else {
            LOGGER.debug("No basic authentication token was found for the security scheme: {}." +
                    " You must verify that the properties: {} and {} are properly configured, or the request header: {} is set when the token propagation is enabled.",
                    getName(), getCanonicalAuthConfigPropertyName(USER_NAME, getOpenApiSpecId(), getName()),
                    getCanonicalAuthConfigPropertyName(PASSWORD, getOpenApiSpecId(), getName()),
                    getHeaderForPropagation(getOpenApiSpecId(), getName()));
        }
    }
}
