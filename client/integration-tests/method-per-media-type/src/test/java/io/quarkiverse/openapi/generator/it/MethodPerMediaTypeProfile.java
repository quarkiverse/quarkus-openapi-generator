package io.quarkiverse.openapi.generator.it;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class MethodPerMediaTypeProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "quarkus.openapi-generator.codegen.spec.openapi_multiple_media_types_json.method-per-media-type",
                "true",
                "quarkus.openapi-generator.codegen.spec.openapi_multiple_media_types_yaml.method-per-media-type",
                "true");
    }
}
