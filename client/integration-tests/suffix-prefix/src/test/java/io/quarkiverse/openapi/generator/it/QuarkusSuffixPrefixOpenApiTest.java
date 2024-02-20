package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class QuarkusSuffixPrefixOpenApiTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "org.openapi.quarkus.quarkus_suffix_prefix_openapi_yaml.api.ReactiveGreetingResourceCustomApiSuffix",
            "org.openapi.quarkus.quarkus_suffix_prefix_openapi_yaml.model.CustomModelPrefixLinkCustomModelSuffix"
    })
    void apiIsBeingGenerated(String className) {
        assertThatCode(() -> Class.forName(className))
                .doesNotThrowAnyException();
    }
}
