package io.quarkiverse.openapi.server.generator.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class DefaultResourceImplTest {

    @Test
    public void testHello() {
        given()
                .when()
                .accept(ContentType.JSON)
                .get("/hello")
                .then()
                .statusCode(200)
                .body(is("Hello, World!"));
    }

    @Test
    public void testCreate() {
        given()
                .when()
                .accept(ContentType.JSON)
                .post("/create")
                .then()
                .statusCode(201);
    }

    @Test
    public void testForbidden() {
        given()
                .when()
                .get("/void")
                .then()
                .statusCode(204);
    }

    @Test
    public void testCallList() {
        given()
                .when()
                .accept(ContentType.JSON)
                .get("/list")
                .then()
                .statusCode(200);
    }

    @Test
    public void testMap() {
        given()
                .when()
                .accept(ContentType.JSON)
                .get("/map")
                .then()
                .statusCode(200);
    }
}
