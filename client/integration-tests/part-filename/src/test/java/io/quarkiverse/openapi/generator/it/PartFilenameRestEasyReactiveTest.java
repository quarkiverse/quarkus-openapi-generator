package io.quarkiverse.openapi.generator.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.PartFilename;
import org.jboss.resteasy.reactive.PartType;
import org.junit.jupiter.api.Tag;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@Tag("resteasy-reactive")
class PartFilenameRestEasyReactiveTest extends BasePartFilenameTest {

    @Override
    protected void testPartTypeAnnotation(Field field) {
        var partType = field.getAnnotation(PartType.class);
        assertNotNull(partType);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, partType.value());
    }

    @Override
    protected void testPartFilenameAnnotation(Field field, boolean present, String value) {
        var partFilename = field.getAnnotation(PartFilename.class);
        if (present) {
            assertNotNull(partFilename);
            if (value != null) {
                assertEquals(value, partFilename.value());
            }
        } else {
            assertNull(partFilename);
        }
    }
}
