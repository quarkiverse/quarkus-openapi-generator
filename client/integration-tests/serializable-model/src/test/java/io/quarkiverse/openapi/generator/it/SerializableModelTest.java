package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SerializableModelTest {

    @Test
    void verifySerializableIsEnabled() {
        var interfaces = org.acme.serializable.model.Animal.class.getInterfaces();

        assertThat(interfaces).contains(Serializable.class);
    }

    @Test
    void verifySerializableIsNotEnabled() {
        var interfaces = org.acme.non.serializable.model.Animal.class.getInterfaces();

        assertThat(interfaces).doesNotContain(Serializable.class);
    }
}
