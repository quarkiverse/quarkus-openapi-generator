package io.quarkiverse.openapi.generator.providers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkus.arc.DefaultBean;

@DefaultBean
@ApplicationScoped
public class DefaultHeadersProvider implements HeadersProvider {

    @Override
    public MultivaluedMap<String, String> getStringHeaders(OpenApiGeneratorConfig generatorConfig) {
        return new MultivaluedHashMap<>();
    }
}
