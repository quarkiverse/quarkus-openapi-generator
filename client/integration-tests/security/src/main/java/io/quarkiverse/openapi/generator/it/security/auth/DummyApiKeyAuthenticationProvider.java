package io.quarkiverse.openapi.generator.it.security.auth;

import java.io.IOException;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkiverse.openapi.generator.SpecItemConfig;
import io.quarkiverse.openapi.generator.providers.ApiKeyAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.ApiKeyIn;
import io.quarkiverse.openapi.generator.providers.AuthProvider;

@Priority(Priorities.AUTHENTICATION)
public class DummyApiKeyAuthenticationProvider implements ClientRequestFilter {

    @Inject
    OpenApiGeneratorConfig generatorConfig;

    private AuthProvider authProvider;

    @PostConstruct
    public void init() {
        authProvider = new ApiKeyAuthenticationProvider("open_weather_custom_security_yaml", "app_id", ApiKeyIn.query, "appid",
                generatorConfig.getItemConfig("open_weather_custom_security_yaml")
                        .flatMap(SpecItemConfig::getAuth).flatMap(x -> x.getItemConfig("app_id")).orElse(null),
                List.of());
    }

    @Override
    public final void filter(ClientRequestContext requestContext) throws IOException {
        authProvider.filter(requestContext);
    }
}
