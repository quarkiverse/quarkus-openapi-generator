package io.quarkiverse.openapi.generator.it;

import javax.inject.Inject;

import org.acme.openapi.api.PetApi;
import org.acme.openapi.model.Pet;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(WiremockPetStore.class)
@QuarkusTest
public class PetStoreTest {

    @RestClient
    @Inject
    PetApi petApi;

    @Test
    public void testGetPetById() {
        final Pet pet = petApi.getPetById(1234L);
        Assertions.assertEquals("Bidu", pet.getName());
        Assertions.assertEquals(Pet.StatusEnum.AVAILABLE, pet.getStatus());
    }
}
