package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.api.PetApi;
import org.openapi.quarkus.model.Pet;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(WiremockPetStore.class)
@QuarkusTest
public class PetStoreTest {

    // injected by quarkus test resource
    WireMockServer petstoreServer;

    @RestClient
    @Inject
    PetApi petApi;

    @Test
    public void testGetPetById() {
        final Pet pet = petApi.getPetById(1234L);
        assertEquals("Bidu", pet.getName());
        assertEquals(Pet.StatusEnum.AVAILABLE, pet.getStatus());

        petstoreServer.verify(getRequestedFor(urlEqualTo("/pet/1234")));
    }
}
