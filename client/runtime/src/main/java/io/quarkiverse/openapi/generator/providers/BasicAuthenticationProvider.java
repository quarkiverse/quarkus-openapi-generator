package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

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
        String basicToken = AuthUtils.basicAuthAccessTokenWithoutPrefix(getUsername(requestContext),
                getPassword(requestContext));

        if (isTokenPropagation()) {
            LOGGER.warn("Token propagation enabled for BasicAuthentication");
            basicToken = sanitizeBasicToken(getTokenForPropagation(requestContext.getHeaders()));
        }

        if (!isEmptyOrBlank(basicToken)) {
            requestContext.getHeaders().remove(HttpHeaders.AUTHORIZATION);
            requestContext.getHeaders().put(HttpHeaders.AUTHORIZATION,
                    Collections.singletonList(AuthUtils.basicAuthAccessToken(basicToken)));
        }

    }
}
