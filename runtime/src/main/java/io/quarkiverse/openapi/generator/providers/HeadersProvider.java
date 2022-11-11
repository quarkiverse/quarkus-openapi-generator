package io.quarkiverse.openapi.generator.providers;

import jakarta.ws.rs.core.MultivaluedMap;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;

public interface HeadersProvider {

    MultivaluedMap<String, String> getStringHeaders(OpenApiGeneratorConfig generatorConfig);

}
