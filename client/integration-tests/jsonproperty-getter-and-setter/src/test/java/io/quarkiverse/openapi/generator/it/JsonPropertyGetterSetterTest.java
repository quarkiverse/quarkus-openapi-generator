package io.quarkiverse.openapi.generator.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

import org.acme.jsonproperty.gettersetter.model.Animal;
import org.acme.jsonproperty.gettersetter.model.Mammal;
import org.acme.jsonproperty.gettersetter.model.Primate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class JsonPropertyGetterSetterTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeAll
    static void setup() {
        OBJECT_MAPPER.findAndRegisterModules();
    }

    @Test
    void verifyGetterSetterWorksOnSnakeCasedFields() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("animal_name", "Lion");

        Animal animal;
        try {
            animal = OBJECT_MAPPER.readValue(jsonObjectBuilder.build().toString(), Animal.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(Animal.class, animal.getClass());
        assertEquals("Lion", animal.getAnimalName());
    }

    @Test
    void verifyGetterSetterWorksOnNonSnakeCasedFields() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        // OffsetDateTime field to verify that non-snake-cased fields also work
        jsonObjectBuilder.add("born", "2020-01-01T00:00:00Z");

        Animal animal;
        try {
            animal = OBJECT_MAPPER.readValue(jsonObjectBuilder.build().toString(), Animal.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(Animal.class, animal.getClass());
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendInstant().toFormatter();
        assertEquals("2020-01-01T00:00:00Z", animal.getBorn().format(formatter));
    }

    @Test
    void verifyJavaReflectionIndicatesJsonPropertyOnSettersAndGetters() {
        try {
            var getter = Animal.class.getMethod("getAnimalName");
            var setter = Animal.class.getMethod("setAnimalName", String.class);

            var getterAnnotation = getter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            var setterAnnotation = setter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);

            assertEquals("animal_name", getterAnnotation.value());
            assertEquals("animal_name", setterAnnotation.value());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            var getter = Primate.class.getMethod("getAnimalName");
            var setter = Primate.class.getMethod("setAnimalName", String.class);

            var getterAnnotation = getter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            var setterAnnotation = setter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);

            assertEquals("animal_name", getterAnnotation.value());
            assertEquals("animal_name", setterAnnotation.value());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            var getter = Mammal.class.getMethod("getAnimalName");
            var setter = Mammal.class.getMethod("setAnimalName", String.class);

            var getterAnnotation = getter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            var setterAnnotation = setter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);

            assertEquals("animal_name", getterAnnotation.value());
            assertEquals("animal_name", setterAnnotation.value());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void verifyJavaReflectionIndicatesJsonPropertyOnSettersAndGettersQueryParam() {
        try {
            var getter = Animal.AnimalQueryParam.class.getMethod("getAnimalName");
            var setter = Animal.AnimalQueryParam.class.getMethod("setAnimalName", String.class);

            var getterAnnotation = getter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            var setterAnnotation = setter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);

            assertEquals("animal_name", getterAnnotation.value());
            assertEquals("animal_name", setterAnnotation.value());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            var getter = Mammal.MammalQueryParam.class.getMethod("getAnimalName");
            var setter = Mammal.MammalQueryParam.class.getMethod("setAnimalName", String.class);

            var getterAnnotation = getter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            var setterAnnotation = setter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);

            assertEquals("animal_name", getterAnnotation.value());
            assertEquals("animal_name", setterAnnotation.value());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            var getter = Primate.PrimateQueryParam.class.getMethod("getAnimalName");
            var setter = Primate.PrimateQueryParam.class.getMethod("setAnimalName", String.class);

            var getterAnnotation = getter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            var setterAnnotation = setter.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);

            assertEquals("animal_name", getterAnnotation.value());
            assertEquals("animal_name", setterAnnotation.value());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
