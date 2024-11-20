package io.quarkiverse.openapi.moqu;

/**
 * Enum representing the type of a parameter in an HTTP request, indicating its location.
 * The parameter can be part of the path, query string, or headers.
 */
public enum ParameterType {

    /**
     * Indicates that the parameter is part of the URL path.
     */
    PATH("path"),

    /**
     * Indicates that the parameter is part of the query string.
     */
    QUERY("query"),

    /**
     * Indicates that the parameter is part of the HTTP headers.
     */
    HEADER("header");

    private final String value;

    /**
     * Constructs a {@code ParameterType} with the given string value representing the parameter location.
     *
     * @param value the string value corresponding to the parameter type.
     */
    ParameterType(String value) {
        this.value = value;
    }

    /**
     * Returns the string value associated with this {@code ParameterType}.
     *
     * @return the string representation of the parameter type.
     */
    public String value() {
        return this.value;
    }
}