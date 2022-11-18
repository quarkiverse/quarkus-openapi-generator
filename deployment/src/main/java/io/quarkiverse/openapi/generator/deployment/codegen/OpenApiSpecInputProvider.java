package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.xapi.generator.deployment.codegen.XApiSpecInputProvider;

/**
 * Provider interface for clients to dynamically provide their own OpenAPI specification files.
 */
public interface OpenApiSpecInputProvider extends XApiSpecInputProvider<SpecInputModel> {

}
