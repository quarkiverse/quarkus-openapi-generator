package io.quarkiverse.openapi.moqu.model;

/**
 * Represents an HTTP operation.
 * <p>
 *
 * @param httpMethod the HTTP verb used for the current {@link Operation}.
 */
public record Operation(String httpMethod) {
}
