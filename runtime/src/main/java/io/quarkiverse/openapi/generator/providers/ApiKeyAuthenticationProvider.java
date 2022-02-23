package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;

import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import io.quarkus.arc.Priority;

@Priority(Priorities.AUTHENTICATION)
public class ApiKeyAuthenticationProvider implements ClientRequestFilter {

    private final ApiKeyIn specIn;

    public ApiKeyAuthenticationProvider(final ApiKeyIn specIn) {
        this.specIn = specIn;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {

    }
}
