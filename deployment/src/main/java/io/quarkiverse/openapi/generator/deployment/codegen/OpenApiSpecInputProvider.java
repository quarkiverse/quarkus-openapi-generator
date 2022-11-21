package io.quarkiverse.openapi.generator.deployment.codegen;

import io.quarkiverse.spec.generator.deployment.codegen.BaseApiSpecInputProvider;

/**
 * Provider interface for clients to dynamically provide their own OpenAPI specification files.
 */
public interface OpenApiSpecInputProvider extends BaseApiSpecInputProvider<SpecInputModel> {
}
