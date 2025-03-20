package io.quarkiverse.openapi.moqu.model;

import io.quarkiverse.openapi.moqu.ParameterType;

/**
 * Represents an HTTP request parameter with a key, value, and location indicating where the parameter is used.
 *
 * @param key the key of the parameter (e.g., "id", "query").
 * @param value the value of the parameter associated with the key.
 * @param where the location of the parameter in the request (e.g., query string, path, header), defined by
 *        {@link ParameterType}.
 */
public record Parameter(String key, String value, ParameterType where) {
}