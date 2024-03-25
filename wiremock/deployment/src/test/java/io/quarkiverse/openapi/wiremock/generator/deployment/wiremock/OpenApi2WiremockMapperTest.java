package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model.Stub;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

class OpenApi2WiremockMapperTest {

    @Test
    @DisplayName("should map OpenAPI path with GET /ping to Wiremock stubbing correctly")
    void should_map_openapi_path_with_get_ping_to_wiremock_stubbing_correctly() throws URISyntaxException {
        // given
        OpenAPI openAPI = readOpenAPI("openapi/should_map_openapi_path_with_get_ping_to_wiremock_stubbing_correctly.yaml");
        OpenApi2WiremockMapper openApi2WiremockMapper = new OpenApi2WiremockMapper(openAPI);

        // when
        List<Stub> stubs = openApi2WiremockMapper.generateWiremockStubs();

        // then
        Stub stub = stubs.stream().findFirst().orElseThrow();

        Assertions.assertEquals(1, stubs.size());
        Assertions.assertEquals("/ping", stub.urlPath());
        Assertions.assertEquals("GET", stub.method());
    }

    @Test
    @DisplayName("Should map two paths to two stubs correctly")
    public void should_map_two_paths_to_two_stubs_correctly() throws URISyntaxException {
        // given
        OpenAPI openAPI = readOpenAPI("openapi/should_map_two_paths_to_two_stubbing_correctly.yaml");
        OpenApi2WiremockMapper openApi2WiremockMapper = new OpenApi2WiremockMapper(openAPI);

        // when
        List<Stub> stubs = openApi2WiremockMapper.generateWiremockStubs();

        // then
        Assertions.assertEquals(2, stubs.size());

        Assertions.assertTrue(
                stubs.stream().anyMatch(stub -> stub.urlPath().equalsIgnoreCase("/users/1")
                        && stub.method().equalsIgnoreCase("GET")));

        Assertions.assertTrue(
                stubs.stream().anyMatch(stub -> stub.urlPath().equalsIgnoreCase("/users/1")
                        && stub.method().equalsIgnoreCase("DELETE")));
    }

    @Test
    @DisplayName("Should map ApiResponse $ref to empty object when the $ref is not an object")
    void should_map_api_response_$ref_to_empty_object_when_the_$ref_is_not_an_object() throws URISyntaxException {
        // given
        OpenAPI openAPI = readOpenAPI(
                "openapi/should_map_api_response_$ref_to_empty_object_when_the_$ref_is_not_an_object.json");
        OpenApi2WiremockMapper openApi2WiremockMapper = new OpenApi2WiremockMapper(openAPI);

        // arrange
        List<Stub> stubs = openApi2WiremockMapper.generateWiremockStubs();
        Stub stub = stubs.stream().findFirst().orElse(null);

        // assert
        Assertions.assertNotNull(stub);

        Assertions.assertEquals("{}", stub.response().getBody());
    }

    @Test
    @DisplayName("Should map ApiResponse $ref to user object when the $ref is an object")
    void should_map_api_response_$ref_to_user_object_when_the_$ref_is_an_object() throws URISyntaxException {
        // given
        OpenAPI openAPI = readOpenAPI("openapi/should_map_api_response_$ref_to_user_object_when_the_$ref_is_an_object.json");
        OpenApi2WiremockMapper openApi2WiremockMapper = new OpenApi2WiremockMapper(openAPI);

        // arrange
        List<Stub> stubs = openApi2WiremockMapper.generateWiremockStubs();
        Stub stub = stubs.stream().findFirst().orElse(null);

        // assert
        Assertions.assertNotNull(stub);
        Assertions.assertEquals("{\"user\":\"John Doe\"}", stub.response().getBody());
    }

    @Test
    @DisplayName("Should get the lowest status code when there is two")
    void should_get_the_lowest_status_code_when_there_is_two() throws URISyntaxException {
        // given
        OpenAPI openAPI = readOpenAPI("openapi/should_get_the_lowest_status_code_when_there_is_two.yaml");
        OpenApi2WiremockMapper openApi2WiremockMapper = new OpenApi2WiremockMapper(openAPI);

        // arrange
        List<Stub> stubs = openApi2WiremockMapper.generateWiremockStubs();
        Stub stub = stubs.stream().findFirst().orElse(null);

        // assert
        Assertions.assertNotNull(stub);
        Assertions.assertEquals(200, stub.response().getStatus());
    }

    OpenAPI readOpenAPI(final String file) throws URISyntaxException {
        URI uri = Objects
                .requireNonNull(this.getClass().getClassLoader().getResource(file), "File %s not found ;/".formatted(file))
                .toURI();
        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        return parser.read(uri.toString());
    }

}
