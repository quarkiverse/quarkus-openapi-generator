package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model;

import java.util.Objects;

/**
 * Represents a basic Wiremock stub.
 * See <a href="https://wiremock.org/docs/stubbing/#basic-stubbing">here</a>
 * the official Wiremock documentation for more details.
 */
public record Stubbing(Request request, Response response) {

    public Stubbing(Request request, Response response) {
        this.request = Objects.requireNonNull(request);
        this.response = Objects.requireNonNull(response);
    }

    public String urlPath() {
        return this.request.getUrlPathTemplate();
    }

    public String method() {
        return this.request.getMethod();
    }

    public static class StubbingBuilder {
        private Request request;
        private Response response;

        public StubbingBuilder request(Request request) {
            this.request = request;
            return this;
        }

        public StubbingBuilder response(Response response) {
            this.response = response;
            return this;
        }

        public Stubbing build() {
            return new Stubbing(request, response);
        }
    }
}