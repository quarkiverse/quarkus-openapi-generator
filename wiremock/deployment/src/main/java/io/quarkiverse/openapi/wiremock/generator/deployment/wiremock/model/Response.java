package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Response {

    private final Integer status;
    private final String body;

    private Response(final Integer status, final String body) {
        this.status = status;
        this.body = body;
    }

    public static Response create(final Integer status, final String body) {
        return new Response(
                status, body);
    }

    public Integer getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }
}