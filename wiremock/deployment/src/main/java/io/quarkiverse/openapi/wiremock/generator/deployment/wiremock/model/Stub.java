package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model;

import java.util.Objects;

/**
 * Represents a basic Wiremock stub.
 * See <a href="https://wiremock.org/docs/stubbing/#basic-stubbing">here</a>
 * the official Wiremock documentation for more details.
 */
public record Stub(Request request, Response response) {

    public Stub(Request request, Response response) {
        this.request = Objects.requireNonNull(request);
        this.response = Objects.requireNonNull(response);
    }

    public String urlPath() {
        return this.request.urlPathTemplate();
    }

    public String method() {
        return this.request.method();
    }
}