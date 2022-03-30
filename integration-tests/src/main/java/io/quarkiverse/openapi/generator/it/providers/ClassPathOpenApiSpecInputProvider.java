package io.quarkiverse.openapi.generator.it.providers;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import io.quarkiverse.openapi.generator.OpenApiSpecInputProvider;

/**
 * Class used during tests to read the spec file from an alternative input.
 */
public class ClassPathOpenApiSpecInputProvider implements OpenApiSpecInputProvider {
    @Override
    public List<InputStream> read() {
        return Collections.singletonList(this.getClass().getResourceAsStream("petstore.json"));
    }
}
