package io.quarkiverse.openapi.server.generator.it;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.petstore.beans.Pet;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

@QuarkusTest
public class PetStoreTest {

    @Test
    public void testBeans() {

        Pet pet = new Pet().withName("test").withId(1234L).withStatus(Pet.Status.AVAILABLE);

        Assertions.assertEquals(1234L, pet.getId());
        Assertions.assertEquals("test", pet.getName());
        Assertions.assertEquals(Pet.Status.AVAILABLE, pet.getStatus());
    }

    @Test
    public void testRestResponseGeneratedWithOperationIdsReturnType() {
        Pet pet = new Pet().withName("test").withId(1234L).withStatus(Pet.Status.AVAILABLE);
        RestAssured.given()
                .body(pet)
                .get("/pet/findByStatus")
                .then()
                .statusCode(200);
    }
}
