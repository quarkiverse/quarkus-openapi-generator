package io.quarkiverse.openapi.generator.it;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class GenerateFlagsTest {

    @Test
    void testEndpointIsInPlace() throws ClassNotFoundException {
        Class.forName("org.openapi.quarkus.generate_models_false_yaml.api.ExistentEndpointApi");
    }

    @Test
    void testModelIsInPlace() throws ClassNotFoundException {
        Class.forName("org.openapi.quarkus.generate_apis_false_yaml.model.ExistentObject");
    }

    @Test
    void testEndpointIsNotInPlace() {
        Assertions.assertThatThrownBy(() -> {
            Class.forName("org.openapi.quarkus.generate_apis_false_yaml.model.NonExistentEndpointApi");
        }).isInstanceOf(ClassNotFoundException.class);
    }

    @Test
    void testModelIsNotInPlace() {
        Assertions.assertThatThrownBy(() -> {
            Class.forName("org.openapi.quarkus.generate_models_false_yaml.api.NonExistentObject");
        }).isInstanceOf(ClassNotFoundException.class);
    }

}
