package io.quarkiverse.openapi.moqu.wiremock.model;

import java.util.Map;

public record WiremockResponse(Integer status,
        String body,
        Map<String, Object> headers) {
}
