package io.quarkiverse.openapi.generator.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.OffsetDateTime;

import org.acme.equals.hashcode.model.Animal;
import org.acme.equals.hashcode.model.RegisterDeviceV3Request;
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

    @Test
    void verifyModelPropertyNameDoesNotBreakEquals() {
        var object1 = new RegisterDeviceV3Request();
        object1.setModel("iPhone");
        object1.setSerialNumber("SN-001");

        var object2 = new RegisterDeviceV3Request();
        object2.setModel("iPhone");
        object2.setSerialNumber("SN-001");

        var object3 = new RegisterDeviceV3Request();
        object3.setModel("iPad");
        object3.setSerialNumber("SN-001");

        assertEquals(object1, object2);
        assertNotEquals(object1, object3);
        assertNotEquals(object2, object3);
    }

    @Test
    void verifyModelPropertyNameDoesNotBreakHashCode() {
        var object1 = new RegisterDeviceV3Request();
        object1.setModel("iPhone");
        object1.setSerialNumber("SN-001");

        var object2 = new RegisterDeviceV3Request();
        object2.setModel("iPhone");
        object2.setSerialNumber("SN-001");

        assertEquals(object1.hashCode(), object2.hashCode());
    }
}
