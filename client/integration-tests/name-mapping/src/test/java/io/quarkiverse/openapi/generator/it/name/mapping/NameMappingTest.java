package io.quarkiverse.openapi.generator.it.name.mapping;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

import org.acme.openapi.namemapping.model.NameMappingModel;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class NameMappingTest {

    @Test
    public void testTypeFieldExists() throws NoSuchFieldException {
        Field typeField = NameMappingModel.class.getDeclaredField("type");
        assertNotNull(typeField);
    }

    @Test
    public void testCustomTypeFieldExists() throws NoSuchFieldException {
        Field customTypeField = NameMappingModel.class.getDeclaredField("customType");
        assertNotNull(customTypeField);
    }

    @Test
    public void testOriginalUnderscoreTypeFieldIsRemapped() {
        assertThrows(NoSuchFieldException.class, () -> NameMappingModel.class.getDeclaredField("_type"));
    }
}
