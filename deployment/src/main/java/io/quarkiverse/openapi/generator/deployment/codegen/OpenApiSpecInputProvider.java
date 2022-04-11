package io.quarkiverse.openapi.generator.deployment.codegen;

import java.io.InputStream;
import java.util.List;

import io.quarkus.deployment.CodeGenContext;

/**
 * Provider interface for clients to dynamically provide their own OpenAPI specification files.
 */
public interface OpenApiSpecInputProvider {

    /**
     * Fetch OpenAPI specification files from a given source.
     *
     * @return a list of spec files in {@link InputStream} format.
     */
    List<SpecInputModel> read(CodeGenContext context);

}
