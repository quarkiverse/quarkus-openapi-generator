package io.quarkiverse.openapi.server.generator.it;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MultiSpecGenerationTest {

    @Test
    public void testPetstoreSourcesGenerated() {
        assertTrue(Files.exists(
                Path.of("target/generated-sources/quarkus-openapi-generator-server/org/acme/petstore/model/Pet.java")));
        assertTrue(Files.exists(
                Path.of("target/generated-sources/quarkus-openapi-generator-server/org/acme/petstore/resources/PetResource.java")));
    }

    @Test
    public void testAnimalSourcesGenerated() {
        assertTrue(Files.exists(
                Path.of("target/generated-sources/quarkus-openapi-generator-server/org/acme/animal/model/Animal.java")));
        assertTrue(Files.exists(
                Path.of("target/generated-sources/quarkus-openapi-generator-server/org/acme/animal/resources/DefaultResource.java")));
    }
}
