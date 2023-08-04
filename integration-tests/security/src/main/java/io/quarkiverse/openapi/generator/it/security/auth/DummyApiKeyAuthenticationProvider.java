package io.quarkiverse.openapi.generator.it.security.auth;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkiverse.openapi.generator.providers.ApiKeyAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.ApiKeyIn;
import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkus.arc.Priority;

@Priority(Priorities.AUTHENTICATION)
public class DummyApiKeyAuthenticationProvider implements ClientRequestFilter {

    @javax.inject.Inject
    OpenApiGeneratorConfig generatorConfig;

    private AuthProvider authProvider;

    @PostConstruct
    public void init() {
        authProvider = new ApiKeyAuthenticationProvider("open_weather_custom_security_yaml", "app_id", ApiKeyIn.query, "appid",
                generatorConfig);
    }

    @Override
    public final void filter(ClientRequestContext requestContext) throws IOException {
        authProvider.filter(requestContext);
    }
}
