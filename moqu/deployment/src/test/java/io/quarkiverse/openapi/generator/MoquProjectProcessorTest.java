package io.quarkiverse.openapi.generator;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;

public class MoquProjectProcessorTest {

    @RegisterExtension
    static final QuarkusDevModeTest unitTest = new QuarkusDevModeTest()
            .withApplicationRoot(javaArchive -> javaArchive
                    .addAsResource("api.yaml", "openapi/openapi.yaml")
                    .addAsResource("apiv2.json", "openapi/api.json"));

    @Test
    void testModeAsSee() {
        RestAssured.given()
                .when().get("/q/moqu/yaml/openapi/wiremock-mappings.json?mode=see")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("Alice"))
                .log().ifError();
    }

    @Test
    void testModeAsDownload() {
        RestAssured.given()
                .when().get("/q/moqu/yaml/openapi/wiremock-mappings.json")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("Alice"))
                .log().ifError();
    }

    @Test
    void testModeAsDownloadUsingJson() {
        RestAssured.given()
                .when().get("/q/moqu/json/api/wiremock-mappings.json")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("Alice"))
                .log().ifError();
    }
}
