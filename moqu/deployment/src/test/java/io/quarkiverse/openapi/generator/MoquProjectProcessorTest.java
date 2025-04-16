package io.quarkiverse.openapi.generator;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;

public class MoquProjectProcessorTest {

    private static final String WIREMOCK_MAPPINGS_JSON_PATH = "/q/moqu/json/api/wiremock-mappings.json";

    @RegisterExtension
    static final QuarkusDevModeTest unitTest = new QuarkusDevModeTest()
            .withApplicationRoot(javaArchive -> javaArchive
                    .addAsResource("api.yaml", "openapi/openapi.yaml")
                    .addAsResource("apiv2.json", "openapi/api.json")
                    .addAsResource("application.properties", "application.properties"));

    // --- TESTE DE MUDANÇA DE CONFIGURAÇÃO AJUSTADO ---
    @Test
    // Executa depois dos testes básicos
    void testDevModeWatchApplicationPropertiesChange() {
        // 1. Verificar estado inicial (deve usar o default "openapi", não "dir2")
        System.out.println("\n--- Verificando Estado Inicial (Default 'openapi') ---");
        // Endpoint do diretório padrão "openapi" deve existir
        RestAssured.given()
                .when().get("/q/moqu/yaml/openapi/wiremock-mappings.json")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("Alice")) // Conteúdo de api.yaml
                .log().ifValidationFails();

        // Endpoint do diretório alternativo "dir2" NÃO deve existir ainda
        RestAssured.given()
                .when().get("/q/moqu/yaml/dir2/wiremock-mappings.json")
                .then()
                .statusCode(404) // Espera Not Found
                .log().ifValidationFails();

        // 2. Criar/Modificar application.properties para apontar para "dir2"
        System.out.println("\n--- Modificando application.properties para apontar para 'dir2' ---");
        // IMPORTANTE: O valor deve ser o caminho relativo DENTRO do ambiente de teste (target/test-classes)
        String newPropertiesContent = "quarkus.openapi-generator.moqu.resource-dir=target/test-classes/dir2";
        // modifyResourceFile criará o arquivo se ele não existir ou substituirá o conteúdo se existir.
        unitTest.modifyResourceFile("application.properties", content -> newPropertiesContent);

        // 3. Verificar estado após a modificação (deve processar "dir2", não mais "openapi")
        System.out.println("\n--- Verificando Estado Pós-Modificação ('dir2' ativo) ---");
        // Endpoint do diretório padrão "openapi" NÃO deve mais existir
        RestAssured.given()
                .when().get("/q/moqu/yaml/openapi/wiremock-mappings.json")
                .then()
                .statusCode(404) // Espera Not Found
                .log().ifValidationFails();

        // Endpoint do diretório alternativo "dir2" deve existir AGORA
        RestAssured.given()
                .when().get("/q/moqu/yaml/dir2/wiremock-mappings.json")
                .then()
                .statusCode(200)
                .body(Matchers.containsString("Directory 2")) // Conteúdo de dir2/spec2.yaml
                .log().ifValidationFails();

        // Opcional: Remover/Limpar application.properties para retornar ao default (pode ser útil se houver mais testes depois)
        System.out.println("\n--- Revertendo application.properties ---");
        unitTest.modifyResourceFile("application.properties", content -> "# Properties reverted or cleared");
    }

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
                .when().get(WIREMOCK_MAPPINGS_JSON_PATH)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("Alice"))
                .log().ifError();
    }

    @Test
    void testDevModeWatchOpenApiFiles() {
        RestAssured.given()
                .when().get(WIREMOCK_MAPPINGS_JSON_PATH)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("80"));

        unitTest.modifyResourceFile("openapi/api.json", (content) -> content.replace("80", "77"));

        RestAssured.given()
                .when().get(WIREMOCK_MAPPINGS_JSON_PATH)
                .then()
                .statusCode(200)
                .body(Matchers.containsString("77"));
    }
}
