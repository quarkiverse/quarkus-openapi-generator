package io.quarkiverse.openapi.moqu;

import static io.swagger.v3.parser.util.SchemaTypeUtil.INTEGER_TYPE;
import static io.swagger.v3.parser.util.SchemaTypeUtil.OBJECT_TYPE;
import static io.swagger.v3.parser.util.SchemaTypeUtil.STRING_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import io.quarkiverse.openapi.moqu.model.Header;
import io.quarkiverse.openapi.moqu.model.Request;
import io.quarkiverse.openapi.moqu.model.RequestResponsePair;
import io.quarkiverse.openapi.moqu.model.Response;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class OpenAPIMoquImporter implements MoquImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAPIMoquImporter.class);
    private static final String HTTP_HEADER_ACCEPT = "Accept";
    private static final String REFERENCE_PREFIX = "#/components/schemas/";

    @Override
    public Moqu parse(String content) {

        SwaggerParseResult swaggerParseResult = new OpenAPIV3Parser().readContents(content);

        if (LOGGER.isDebugEnabled()) {
            for (String message : swaggerParseResult.getMessages()) {
                LOGGER.debug("[context:SwaggerParseResult] {}", message);
            }
        }

        OpenAPI openAPI = swaggerParseResult.getOpenAPI();

        if (Objects.isNull(openAPI)) {
            throw new IllegalArgumentException("Cannot parse OpenAPI V3 content: " + content);
        }

        return new Moqu(
                getRequestResponsePairs(openAPI));
    }

    private List<RequestResponsePair> getRequestResponsePairs(OpenAPI openAPI) {
        Map<Request, Response> requestResponsePairs = new HashMap<>();

        Map<String, Schema> localSchemas = getSchemas(openAPI);

        Set<Map.Entry<String, PathItem>> entries = Optional.ofNullable(openAPI.getPaths())
                .orElseThrow(IllegalArgumentException::new)
                .entrySet();

        for (Map.Entry<String, PathItem> entry : entries) {

            for (Map.Entry<PathItem.HttpMethod, Operation> httpMethodOperation : entry.getValue().readOperationsMap()
                    .entrySet()) {

                if (!Objects.isNull(httpMethodOperation.getValue().getResponses())) {

                    Set<Map.Entry<String, ApiResponse>> statusApiResponses = httpMethodOperation.getValue().getResponses()
                            .entrySet();

                    for (Map.Entry<String, ApiResponse> statusApiResponse : statusApiResponses) {

                        if (Objects.isNull(statusApiResponse.getValue())) {
                            continue;
                        }

                        Map<String, Multimap<String, String>> examplesOnPath = extractParameters(httpMethodOperation.getValue(),
                                ParameterType.PATH);

                        requestResponsePairs.putAll(getContentRequestResponsePairs(statusApiResponse, examplesOnPath,
                                httpMethodOperation.getKey(), entry.getKey(), localSchemas));
                    }
                }
            }
        }

        return requestResponsePairs.entrySet().stream().map(entry -> new RequestResponsePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Map<String, Schema> getSchemas(OpenAPI openAPI) {
        if (openAPI.getComponents() == null) {
            return Map.of();
        }
        return Objects.requireNonNullElse(openAPI.getComponents().getSchemas(), Map.of());
    }

    private int tryGetStatusCode(Map.Entry<String, ApiResponse> statusApiResponse) {
        try {
            return Integer.parseInt(statusApiResponse.getKey());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid status code: " + statusApiResponse.getKey());
        }
    }

    private Map<String, Multimap<String, String>> extractParameters(Operation operation, ParameterType parameterType) {
        List<Parameter> parameters = Optional.ofNullable(operation.getParameters()).orElse(Collections.emptyList());
        Map<String, Multimap<String, String>> finalParameters = new HashMap<>();

        for (Parameter parameter : parameters) {
            if (isEligibleForExtraction(parameter, parameterType)) {

                Set<String> exampleNames = parameter.getExamples().keySet();
                for (String exampleName : exampleNames) {

                    Example example = parameter.getExamples().get(exampleName);

                    Object object = example.getValue();
                    String value = resolveContent(object);
                    finalParameters.computeIfAbsent(exampleName,
                            k -> ArrayListMultimap.create()).put(parameter.getName(), value);
                }
            }
        }

        return finalParameters;
    }

    private boolean isEligibleForExtraction(Parameter parameter, ParameterType type) {
        return parameter.getIn().equals(type.value()) && !Objects.isNull(parameter.getExamples());
    }

    private Map<Request, Response> getContentRequestResponsePairs(Map.Entry<String, ApiResponse> statusApiResponse,
            Map<String, Multimap<String, String>> parametersOnPath, PathItem.HttpMethod httpMethod, String url,
            Map<String, Schema> localSchemas) {
        Map<Request, Response> requestResponseMap = new HashMap<>();

        ApiResponse apiResponse = statusApiResponse.getValue();

        int statusCode = tryGetStatusCode(statusApiResponse);

        for (Map.Entry<String, MediaType> entry : apiResponse.getContent().entrySet()) {
            String contentType = entry.getKey();
            MediaType mediaType = entry.getValue();
            Map<String, Example> examples = Optional.ofNullable(mediaType.getExamples()).orElse(Collections.emptyMap());

            examples.forEach((exampleName, example) -> {

                String content = resolveContent(localSchemas, example);

                Response response = new Response(
                        exampleName,
                        mediaType,
                        statusCode,
                        content,
                        List.of());

                Multimap<String, String> onPath = parametersOnPath.get(exampleName);
                List<io.quarkiverse.openapi.moqu.model.Parameter> reqParams = new ArrayList<>();

                if (onPath != null) {
                    for (Map.Entry<String, String> paramEntry : onPath.entries()) {
                        io.quarkiverse.openapi.moqu.model.Parameter parameter = new io.quarkiverse.openapi.moqu.model.Parameter(
                                paramEntry.getKey(),
                                paramEntry.getValue(),
                                ParameterType.PATH);
                        reqParams.add(parameter);
                    }
                }

                List<io.quarkiverse.openapi.moqu.model.Parameter> parameters = reqParams.stream()
                        .filter(reqParam -> reqParam.where().equals(ParameterType.PATH)).toList();
                String finalUrl = resolveUrlParameters(url, parameters);
                Request request = new Request(
                        finalUrl,
                        httpMethod.name(),
                        exampleName,
                        new Header(HTTP_HEADER_ACCEPT, List.of(contentType)),
                        reqParams);
                requestResponseMap.put(request, response);
            });
        }

        return requestResponseMap;
    }

    private String resolveContent(Map<String, Schema> localSchemas, Example example) {
        if (!Strings.isNullOrEmpty(example.get$ref())) {
            return resolveRef(example.get$ref(), localSchemas);
        } else {
            return resolveContent(example.getValue());
        }
    }

    private String resolveUrlParameters(String url, List<io.quarkiverse.openapi.moqu.model.Parameter> parameters) {
        for (io.quarkiverse.openapi.moqu.model.Parameter parameter : parameters) {
            String placeholder = "{%s}".formatted(parameter.key());
            url = url.replace(placeholder, parameter.value());
        }
        return url;
    }

    private String resolveRef(String ref, Map<String, Schema> localSchemas) {
        if (!ref.startsWith(REFERENCE_PREFIX)) {
            throw new IllegalArgumentException(
                    "There is no support for external $ref schemas. Please, configure the %s as local schema"
                            .formatted(ref));
        }

        String refName = ref.substring(REFERENCE_PREFIX.length(), ref.length());

        Schema schema = localSchemas.get(refName);

        if (schema == null) {
            throw new IllegalArgumentException("Schema not found: " + refName);
        }

        return generateResponseBodyFromRefSchema(schema);
    }

    private String resolveContent(Object object) {
        if (object instanceof String) {
            return (String) object;
        }
        if (object instanceof Integer) {
            return String.valueOf((Integer) object);
        }
        throw new IllegalArgumentException("Object is not a String");
    }

    private static String generateResponseBodyFromRefSchema(final Schema<?> schema) {
        String schemaType = Optional.ofNullable(schema.getType()).orElse(OBJECT_TYPE);
        return switch (schemaType) {
            case STRING_TYPE, INTEGER_TYPE -> (String) schema.getExample();
            case OBJECT_TYPE -> SchemaReader.readObjectExample(schema);
            default -> "";
        };
    }
}
