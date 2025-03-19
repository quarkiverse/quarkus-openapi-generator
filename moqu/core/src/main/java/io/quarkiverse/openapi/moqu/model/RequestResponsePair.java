package io.quarkiverse.openapi.moqu.model;

/**
 * Represents a pair of an HTTP request and its corresponding response.
 *
 * @param request the HTTP request that was sent.
 * @param response the HTTP response received for the given request.
 */
public record RequestResponsePair(Request request, Response response) {
}