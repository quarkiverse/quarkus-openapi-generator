package io.quarkiverse.openapi.generator.providers;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

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
