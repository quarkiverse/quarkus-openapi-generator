package io.quarkiverse.openapi.generator.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import org.acme.non.equals.hashcode.model.Animal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class VerifyGenerationTest {

    @ParameterizedTest
    @MethodSource("provideStringsForIsBlank")
    void verify(Class<?> clazz, String methodName, Class<?> expectedDeclaringClass) {
        var equalsMethod = getMethod(clazz, methodName);

        Class<?> declaringClass = equalsMethod.getDeclaringClass();

        assertEquals(declaringClass, expectedDeclaringClass);
    }

    private static Stream<Arguments> provideStringsForIsBlank() {
        return Stream.of(
                Arguments.of(Animal.class, "equals", Object.class),
                Arguments.of(Animal.class, "hashCode", Object.class),
                Arguments.of(org.acme.equals.hashcode.model.Animal.class, "equals",
                        org.acme.equals.hashcode.model.Animal.class),
                Arguments.of(org.acme.equals.hashcode.model.Animal.class, "hashCode",
                        org.acme.equals.hashcode.model.Animal.class));
    }

    public static Method getMethod(Class<?> clazz, String methodName) {
        var methods = clazz.getMethods();

        return Arrays.stream(methods)
                .filter(method -> method.getName().equals(methodName))
                .findAny()
                .orElseThrow();
    }
}
