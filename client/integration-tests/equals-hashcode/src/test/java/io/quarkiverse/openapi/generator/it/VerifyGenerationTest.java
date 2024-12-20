package io.quarkiverse.openapi.generator.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class VerifyGenerationTest {

    @Test
    void verifyThatGeneratedModelDoesntHaveMethods() {
        var equalsMethod = getMethod(org.acme.equals.hashcode.model.Animal.class, "equals");
        var hashCodeMethod = getMethod(org.acme.equals.hashcode.model.Animal.class, "hashCode");

        assertEquals(equalsMethod.getDeclaringClass(), org.acme.equals.hashcode.model.Animal.class);
        assertEquals(hashCodeMethod.getDeclaringClass(), org.acme.equals.hashcode.model.Animal.class);
    }

    @Test
    void verifyThatGeneratedModelDoesHaveMethods() {
        var equalsMethod = getMethod(org.acme.non.equals.hashcode.model.Animal.class, "equals");
        var hashCodeMethod = getMethod(org.acme.non.equals.hashcode.model.Animal.class, "hashCode");

        assertNotEquals(equalsMethod.getDeclaringClass(), org.acme.equals.hashcode.model.Animal.class);
        assertNotEquals(hashCodeMethod.getDeclaringClass(), org.acme.equals.hashcode.model.Animal.class);
    }

    private static Method getMethod(Class<?> clazz, String methodName) {
        var methods = clazz.getMethods();

        return Arrays.stream(methods)
                .filter(method -> method.getName().equals(methodName))
                .findAny()
                .orElseThrow();
    }
}
