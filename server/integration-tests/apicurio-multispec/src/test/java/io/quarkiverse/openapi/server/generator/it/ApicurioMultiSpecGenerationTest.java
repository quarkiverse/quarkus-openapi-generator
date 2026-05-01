package io.quarkiverse.openapi.server.generator.it;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ApicurioMultiSpecGenerationTest {

    @Test
    public void testPetstoreSourcesGenerated() {
        assertTrue(Files.exists(
                Path.of("target/generated-sources/jaxrs/org/acme/petstore/beans/Pet.java")));
        assertTrue(Files.exists(
                Path.of("target/generated-sources/jaxrs/org/acme/petstore/PetResource.java")));
    }

    @Test
    public void testAnimalSourcesGenerated() {
        assertTrue(Files.exists(
                Path.of("target/generated-sources/jaxrs/org/acme/animal/beans/Animal.java")));
        assertTrue(Files.exists(
                Path.of("target/generated-sources/jaxrs/org/acme/animal/AnimalsResource.java")));
    }
}
