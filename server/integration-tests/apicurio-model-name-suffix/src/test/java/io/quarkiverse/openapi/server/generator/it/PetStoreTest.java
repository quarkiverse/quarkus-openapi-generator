package io.quarkiverse.openapi.server.generator.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.anything;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.petstore.beans.PetDto;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class PetStoreTest {

    @Test
    public void testApiWithSuffixedModels() {

        PetDto pet = new PetDto();
        pet.setName("test");
        pet.setId(1234L);
        pet.setStatus(PetDto.Status.AVAILABLE);

        given()
                .when()
                .contentType(ContentType.JSON)
                .body(pet)
                .post("/pet")
                .then()
                .statusCode(204)
                .body(anything());

        final PetDto returnedPet = given()
                .when()
                .accept(ContentType.JSON)
                .get("/pet/1234")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PetDto.class);

        Assertions.assertEquals(pet.getId(), returnedPet.getId());
        Assertions.assertEquals(pet.getName(), returnedPet.getName());
        Assertions.assertEquals(pet.getStatus(), returnedPet.getStatus());
    }

    @Test
    public void resourceInterfaceKeepsItsOriginalName() {
        // PetStoreImpl implements the generated io.petstore.PetResource interface;
        // compiling against it proves the suffix is not applied to resource interfaces.
        Assertions.assertTrue(io.petstore.PetResource.class.isInterface());
    }
}
