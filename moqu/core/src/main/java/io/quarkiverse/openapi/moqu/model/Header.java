package io.quarkiverse.openapi.moqu.model;

import java.util.List;

/**
 * Represents an HTTP header with a name and a set of associated values.
 *
 * @param name the name of the HTTP header (e.g., "Accept", "Content-Type").
 * @param value the set of values associated with the header, allowing multiple values (e.g., "application/json", "text/html").
 */
public record Header(String name, List<String> value) {
}