package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class IncludeExcludeTest {

    @Test
    void onlyNonIgnoredClassesAreGenerated() throws ClassNotFoundException {
        assertThat(Class.forName("org.openapi.quarkus.include_openapi_yaml.api.SimpleOpenApiResourceApi"))
                .isNotNull();

        assertThatCode(() -> Class.forName("org.openapi.quarkus.exclude_openapi_yaml.api.ExcludedOpenApiResourceApi"))
                .isInstanceOf(ClassNotFoundException.class);

        assertThatCode(() -> Class.forName("org.openapi.quarkus.exclude_openapi_2_yaml.api.ExcludedOpenApiResource2Api"))
                .isInstanceOf(ClassNotFoundException.class);
    }
}
