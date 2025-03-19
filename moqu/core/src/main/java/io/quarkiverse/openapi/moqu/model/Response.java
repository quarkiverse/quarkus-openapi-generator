package io.quarkiverse.openapi.moqu.model;

import java.util.List;

import io.swagger.v3.oas.models.media.MediaType;

/**
 * Represents an HTTP response with details such as the example name, media type,
 * status code, content, and headers.
 *
 * @param exampleName the name of the example associated with this response.
 * @param mediaType the media type of the response content (e.g., application/json, text/html),
 *        represented by {@link MediaType}.
 * @param statusCode the HTTP status code of the response (e.g., 200, 404).
 * @param content the body of the response as a string.
 * @param headers the list of headers included in the response.
 */
public record Response(String exampleName, MediaType mediaType, int statusCode,
        String content, List<Header> headers) {
}
