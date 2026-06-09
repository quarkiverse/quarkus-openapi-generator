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
    public void testAnimalSourcesGeneratedWithDefaultPackage() {
        // simple-server.yaml is now auto-discovered (not configured) and uses default package org.acme
        assertTrue(Files.exists(
                Path.of("target/generated-sources/jaxrs/org/acme/AnimalsResource.java")));
    }

    @Test
    public void testUnconfiguredSpecSourcesGeneratedWithDefaultPackage() {
        // unconfigured-server.yaml is auto-discovered (not configured) and uses default package org.acme
        assertTrue(Files.exists(
                Path.of("target/generated-sources/jaxrs/org/acme/UnconfiguredResource.java")));
    }

    @Test
    public void testInventorySpecAutoDiscoveredWithDefaultPackage() {
        // inventory-server.yaml has NO configuration at all - proves auto-discovery works
        assertTrue(Files.exists(
                Path.of("target/generated-sources/jaxrs/org/acme/InventoryResource.java")));
        assertTrue(Files.exists(
                Path.of("target/generated-sources/jaxrs/org/acme/beans/Product.java")));
    }
}
