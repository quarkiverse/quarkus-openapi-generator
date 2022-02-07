package io.quarkiverse.openapi.generator.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class OpenapiGeneratorResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/openapi-generator")
                .then()
                .statusCode(200)
                .body(is("Hello openapi-generator"));
    }
}
