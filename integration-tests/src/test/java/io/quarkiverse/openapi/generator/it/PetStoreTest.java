package io.quarkiverse.openapi.generator.it;

import static io.restassured.RestAssured.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(WiremockPetStore.class)
@QuarkusTest
public class PetStoreTest {

    @Test
    public void testGetPetById() {
        final String petName = when()
                .get("/petstore/pet/name/1234")
                .then()
                .statusCode(200)
                .extract().asString();
        Assertions.assertEquals("Bidu", petName);
    }
}
