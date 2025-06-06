package io.quarkiverse.openapi.generator.it.security.auth;

import java.io.IOException;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import io.quarkiverse.openapi.generator.providers.ApiKeyAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.ApiKeyIn;
import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;

@Priority(Priorities.AUTHENTICATION)
public class DummyApiKeyAuthenticationProvider implements ClientRequestFilter {

    private AuthProvider authProvider;

    @PostConstruct
    public void init() {
        authProvider = new ApiKeyAuthenticationProvider("open_weather_custom_security_yaml", "app_id", ApiKeyIn.query, "appid",
                List.of(), new ConfigCredentialsProvider());
    }

    @Override
    public final void filter(ClientRequestContext requestContext) throws IOException {
        authProvider.filter(requestContext);
    }
}
