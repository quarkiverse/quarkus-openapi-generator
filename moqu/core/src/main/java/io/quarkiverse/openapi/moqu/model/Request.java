package io.quarkiverse.openapi.moqu.model;

import java.util.Collection;

/**
 * Represents an HTTP request with essential details such as URL, HTTP method,
 * example name, accepted header, and parameters.
 *
 * @param url the URL to which the request is sent.
 * @param httpMethod the HTTP method (GET, POST, PUT, DELETE, etc.) used for the request.
 * @param exampleName the name of the example associated with the request.
 * @param accept the "Accept" header, which specifies the expected response format.
 * @param parameters the list of parameters to be included in the request.
 */
public record Request(String url, String httpMethod, String exampleName, Header accept, Collection<Parameter> parameters) {
}
