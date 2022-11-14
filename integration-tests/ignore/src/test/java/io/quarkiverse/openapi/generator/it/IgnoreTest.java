package io.quarkiverse.openapi.generator.it;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@QuarkusTest
class IgnoreTest {

    @Test
    void onlyNonIgnoredClassesAreGenerated() throws ClassNotFoundException {
        assertThat(Class.forName("org.openapi.quarkus.simple_openapi_yaml.api.SimpleOpenApiResourceApi"))
                .isNotNull();

        assertThatCode(() -> Class.forName("org.openapi.quarkus.ignored_openapi_yaml.api.IgnoredOpenApiResourceApi"))
                .isInstanceOf(ClassNotFoundException.class);

        assertThatCode(() -> Class.forName("org.openapi.quarkus.ignored_openapi_2_yaml.api.IgnoredOpenApiResource2Api"))
                .isInstanceOf(ClassNotFoundException.class);
    }
}
