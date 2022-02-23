package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

import io.quarkus.arc.Priority;

/**
 * Base class for generated providers using basic authentication.
 * Username and password should be read by generated configuration properties, which is only known after openapi spec processing
 * during build time.
 */
@Priority(Priorities.AUTHENTICATION)
public abstract class AbstractBasicAuthenticationProvider implements ClientRequestFilter {

    private static final String BASIC_HEADER_PREFIX = "Basic ";

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, getAccessToken());
    }

    private String getAccessToken() {
        return BASIC_HEADER_PREFIX
                + Base64.getEncoder().encodeToString(String.format("%s:%s", this.getUsername(), this.getPassword()).getBytes());
    }

    protected abstract String getUsername();

    protected abstract String getPassword();

}
