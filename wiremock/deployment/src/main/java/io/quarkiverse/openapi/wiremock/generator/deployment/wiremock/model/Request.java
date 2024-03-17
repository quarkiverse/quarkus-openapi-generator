package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model;

import java.util.Objects;

public class Request {
    private final String urlPathTemplate;
    private final String method;

    private Request(final String urlPathTemplate, final String method) {
        this.urlPathTemplate = Objects.requireNonNull(urlPathTemplate);
        this.method = Objects.requireNonNull(method);
    }

    public static Request create(final String urlPath, final String method) {
        return new Request(urlPath, method);
    }

    public String getUrlPathTemplate() {
        return urlPathTemplate;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Request request = (Request) o;
        return Objects.equals(urlPathTemplate, request.urlPathTemplate) && Objects.equals(method, request.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(urlPathTemplate, method);
    }
}