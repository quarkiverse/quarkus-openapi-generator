package io.quarkiverse.openapi.generator.it;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@Tag("resteasy-classic")
class QuarkusSerializableModelRestEasyClassicTest {

    @Test
    void verifySerializableIsEnabled() {
        var interfaces = io.quarkiverse.serializable.model.Animal.class.getInterfaces();

        assertThat(interfaces).contains(Serializable.class);
    }

    @Test
    void verifySerializableIsNotEnabled() {
        var interfaces = io.quarkiverse.non.serializable.model.Animal.class.getInterfaces();

        assertThat(interfaces).doesNotContain(Serializable.class);
    }
}
