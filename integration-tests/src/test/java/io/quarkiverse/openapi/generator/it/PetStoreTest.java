package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.acme.openapi.api.PetApi;
import org.acme.openapi.model.Pet;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkiverse.openapi.generator.providers.AuthUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(WiremockPetStore.class)
@QuarkusTest
public class PetStoreTest {

    // injected by quarkus test resource
    WireMockServer petstoreServer;

    @ConfigProperty(name = "org.acme.openapi.security.auth.basic_auth/username")
    String username;
    @ConfigProperty(name = "org.acme.openapi.security.auth.basic_auth/password")
    String password;

    @RestClient
    @Inject
    PetApi petApi;

    @Test
    public void testGetPetById() {
        final Pet pet = petApi.getPetById(1234L);
        assertEquals("Bidu", pet.getName());
        assertEquals(Pet.StatusEnum.AVAILABLE, pet.getStatus());

        petstoreServer.verify(getRequestedFor(urlEqualTo("/pet/1234"))
                .withHeader(HttpHeaders.AUTHORIZATION,
                        matching(AuthUtils.generateBasicAuthAccessToken(username, password))));
    }
}
