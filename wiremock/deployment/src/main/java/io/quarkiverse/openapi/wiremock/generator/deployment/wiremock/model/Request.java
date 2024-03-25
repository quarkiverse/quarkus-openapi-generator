package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model;

import java.util.Objects;

public record Request(String urlPathTemplate, String method) {

    public Request(String urlPathTemplate, String method) {
        this.urlPathTemplate = Objects.requireNonNull(urlPathTemplate);
        this.method = Objects.requireNonNull(method);
    }

    public static Request create(final String urlPath, final String method) {
        return new Request(urlPath, method);
    }
}
