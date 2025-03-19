package io.quarkiverse.openapi.moqu.wiremock.model;

public record WiremockRequest(
        String method,
        String url) {
}
