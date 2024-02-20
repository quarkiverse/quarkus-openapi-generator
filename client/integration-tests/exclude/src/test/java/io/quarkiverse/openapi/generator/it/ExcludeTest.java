package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ExcludeTest {

    @Test
    void onlyNonExcludedClassesAreGenerated() throws ClassNotFoundException {
        assertThat(Class.forName("org.openapi.quarkus.openapi_yaml.api.SimpleOpenApiResourceApi"))
                .isNotNull();

        assertThatCode(() -> Class.forName("org.openapi.quarkus.exclude_openapi_yaml.api.ExcludedOpenApiResourceApi"))
                .isInstanceOf(ClassNotFoundException.class);
    }
}
