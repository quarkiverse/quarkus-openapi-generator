package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import org.acme.petstore.api.PetApi;
import org.acme.petstore.model.Pet;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkiverse.openapi.generator.testutils.keycloak.KeycloakRealmResourceManager;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;

@WithTestResource(WiremockPetStore.class)
@WithTestResource(KeycloakRealmResourceManager.class)
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
