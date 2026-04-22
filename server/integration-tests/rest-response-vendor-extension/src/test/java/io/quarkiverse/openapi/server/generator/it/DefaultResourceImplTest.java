package io.quarkiverse.openapi.server.generator.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class DefaultResourceImplTest {

    @Test
    public void testStandard() {
        given()
                .when()
                .accept(ContentType.JSON)
                .get("/standard")
                .then()
                .statusCode(200)
                .body(is("standard"));
    }

    @Test
    public void testSpecial() {
        given()
                .when()
                .accept(ContentType.JSON)
                .get("/special")
                .then()
                .statusCode(200)
                .body(is("special"));
    }

    @Test
    public void testReturnTypeExt() {
        given()
                .when()
                .accept(ContentType.JSON)
                .get("/return-type")
                .then()
                .statusCode(200)
                .body(is("return-type"));
    }
}
