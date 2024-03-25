package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock;

import static io.swagger.v3.parser.util.SchemaTypeUtil.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model.Request;
import io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model.Response;
import io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model.Stub;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

public class OpenApi2WiremockMapper {

    private final OpenAPI openAPI;
    public static final String ONLY_SUPPORTED_MEDIA_TYPE = "application/json";
    public static final Integer DEFAULT_STATUS_CODE = 200;

    public OpenApi2WiremockMapper(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    public List<Stub> generateWiremockStubs() {

        List<Stub> stubs = new ArrayList<>();

        Paths paths = openAPI.getPaths();

        Set<Map.Entry<String, PathItem>> pathsEntries = paths.entrySet();

        for (Map.Entry<String, PathItem> pathEntry : pathsEntries) {
            stubs.addAll(generateStubs(pathEntry));
        }

        return stubs;
    }

    private List<Stub> generateStubs(Map.Entry<String, PathItem> entry) {
        Set<Stub> stubs = new HashSet<>();

        String pathName = entry.getKey();

        PathItem pathItem = entry.getValue();

        Map<PathItem.HttpMethod, Operation> operationsMap = pathItem.readOperationsMap();

        operationsMap.forEach(
                (httpMethod, operation) -> {

                    Request request = Request.create(pathName, httpMethod.name());

                    int statusCode = getStatusCode(operation);

                    Response response = Response.create(
                            statusCode,
                            getResponseBody(String.valueOf(statusCode), operation));

                    Stub stub = new Stub(request, response);

                    stubs.add(stub);
                });

        return stubs.stream().toList();
    }

    private String getResponseBodyFromRef(String component) {
        Schema<?> schema = this.openAPI.getComponents()
                .getSchemas()
                .get(getComponentName(component));

        if (!schema.getType().equals(OBJECT_TYPE)) {
            return SchemaReader.EMPTY_JSON_OBJECT;
        }
        return generateResponseBody(schema);
    }

    private String getResponseBody(final String statusCode, final Operation operation) {

        ApiResponse apiResponse = operation.getResponses().get(statusCode);

        if (Objects.isNull(apiResponse.getContent())) {
            return null;
        }

        MediaType applicationJson = new ArrayList<>(apiResponse.getContent().entrySet())
                .stream()
                .filter(media -> media.getKey().equals(OpenApi2WiremockMapper.ONLY_SUPPORTED_MEDIA_TYPE))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "This extension only supports " + OpenApi2WiremockMapper.ONLY_SUPPORTED_MEDIA_TYPE
                                        + "media type"))
                .getValue();

        if (!Objects.isNull(applicationJson.getSchema().get$ref())) {
            return getResponseBodyFromRef(applicationJson.getSchema().get$ref());
        }

        return generateResponseBody(applicationJson.getSchema());
    }

    private static String generateResponseBody(final Schema<?> schema) {
        String schemaType = Optional.ofNullable(schema.getType()).orElse(OBJECT_TYPE);
        return switch (schemaType) {
            case STRING_TYPE, INTEGER_TYPE -> (String) schema.getExample();
            case OBJECT_TYPE -> SchemaReader.readObjectExample(schema);
            default -> "";
        };
    }

    /**
     * This method attempts to retrieve the first status code defined in the {@link Operation} object.
     * Whether the OpenAPI response contains only the "default" status code, this method will return
     * {@link OpenApi2WiremockMapper#DEFAULT_STATUS_CODE}.
     * Whether the OpenAPI response contains more than one status code, the method returns the first one.
     * For example, if the status codes are "200" and after "400", this method will return the status "200".
     *
     * @param operation The {@link Operation} instance from which to extract the status code.
     * @return An {@link Integer} representing the status code. e.g. 200, 300, 400, 500, etc.
     */
    private static int getStatusCode(final Operation operation) {

        final String defaultStatus = "default";
        ArrayList<Map.Entry<String, ApiResponse>> apiResponses = new ArrayList<>(operation.getResponses().entrySet());

        apiResponses.sort(Map.Entry.comparingByKey());

        String statusCode = apiResponses.stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no status on operation"))
                .getKey();

        if (statusCode.contentEquals(defaultStatus)) {
            return DEFAULT_STATUS_CODE;
        }

        try {
            return Integer.parseInt(statusCode);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Was not possible to parse the status code '%s' from OpenAPI Operation", statusCode));
        }
    }

    public String getComponentName(final String ref) {
        String[] split = ref.split("/");
        return split[split.length - 1];
    }

}