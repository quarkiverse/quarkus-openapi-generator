package io.quarkiverse.openapi.generator.providers;

/**
 * @see <a href="https://spec.openapis.org/oas/v3.1.0#fixed-fields-22">OpenAPI Spec - Security Scheme Object - Fixed Fields</a>
 */
public enum ApiKeyIn {
    query,
    header,
    cookie;
}
