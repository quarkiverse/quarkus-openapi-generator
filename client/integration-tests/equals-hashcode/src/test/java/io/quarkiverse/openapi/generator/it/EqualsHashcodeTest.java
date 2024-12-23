package io.quarkiverse.openapi.generator.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.OffsetDateTime;

import org.acme.equals.hashcode.model.Animal;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class EqualsHashcodeTest {

    @Test
    void verifyModelNotEquals() {
        var object1 = new Animal();
        object1.setDeceased(OffsetDateTime.now().minusHours(2));

        var object2 = new Animal();
        object2.setBorn(OffsetDateTime.now().minusYears(1));

        assertNotEquals(object1, object2);
    }

    @Test
    void verifyModelEquals() {
        var offset = OffsetDateTime.now().minusHours(2);

        var object1 = new Animal();
        object1.setDeceased(offset);

        var object2 = new Animal();
        object2.setDeceased(offset);

        assertEquals(object1, object2);
    }

    @Test
    void verifyModelHasHashCode() {
        var offset = OffsetDateTime.now().minusHours(2);

        var object1 = new Animal();
        object1.setDeceased(offset);

        var object2 = new Animal();
        object2.setDeceased(offset);

        assertEquals(object1.hashCode(), object2.hashCode());
    }
}
