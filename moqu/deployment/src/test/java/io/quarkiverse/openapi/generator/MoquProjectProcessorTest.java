package io.quarkiverse.openapi.generator;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;

public class MoquProjectProcessorTest {

    @RegisterExtension
    static final QuarkusDevModeTest unitTest = new QuarkusDevModeTest()
            .withApplicationRoot(javaArchive -> javaArchive.addAsResource(
                    "api.yaml", "src/openapi/openapi.yaml"));

    @Test
    void test() {
        RestAssured.given()
                .when().get("/q/moqu/f/openapi.yaml")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("version: 999-SNAPSHOT"));
    }
}
