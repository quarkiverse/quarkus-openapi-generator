package io.quarkiverse.openapi.server.generator.deployment.codegen.apicurio;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Makes operations whose response is an array of a model declared in {@code components/schemas} return
 * {@code jakarta.ws.rs.core.Response}, by adding the {@code x-codegen-returnType} extension to the response media type.
 * <p>
 * Backs the {@code quarkus.openapi.generator.server.array-as-response} property, which keeps the return type generated
 * by versions 2.20.0 to 2.23.0 for these operations.
 */
final class ArrayAsResponseProcessor {

    static final String RETURN_TYPE_EXTENSION = "x-codegen-returnType";
    static final String RESPONSE_TYPE = "jakarta.ws.rs.core.Response";

    private static final String SCHEMA_REF_PREFIX = "#/components/schemas/";
    private static final Set<String> NON_MODEL_TYPES = Set.of("string", "integer", "number", "boolean", "array");
    private static final int MAX_REF_DEPTH = 16;

    private ArrayAsResponseProcessor() {
    }

    static void process(JsonNode root) {
        for (Map.Entry<String, JsonNode> pathItem : root.path("paths").properties()) {
            for (Map.Entry<String, JsonNode> operation : pathItem.getValue().properties()) {
                processResponses(root, operation.getValue().path("responses"));
            }
        }
        processResponses(root, root.path("components").path("responses"));
    }

    private static void processResponses(JsonNode root, JsonNode responses) {
        for (Map.Entry<String, JsonNode> response : responses.properties()) {
            for (Map.Entry<String, JsonNode> mediaType : response.getValue().path("content").properties()) {
                JsonNode mediaTypeNode = mediaType.getValue();
                // an explicit return type declared in the specification always wins
                if (mediaTypeNode.isObject() && !mediaTypeNode.has(RETURN_TYPE_EXTENSION)
                        && isArrayOfModels(root, mediaTypeNode.path("schema"))) {
                    ((ObjectNode) mediaTypeNode).put(RETURN_TYPE_EXTENSION, RESPONSE_TYPE);
                }
            }
        }
    }

    private static boolean isArrayOfModels(JsonNode root, JsonNode schema) {
        JsonNode array = resolve(root, schema);
        if (!hasType(array, "array")) {
            return false;
        }
        JsonNode items = array.path("items");
        if (!items.has("$ref")) {
            // inline items are not models: scalars keep their List<T> return type
            return false;
        }
        JsonNode model = resolve(root, items);
        return !model.isMissingNode() && NON_MODEL_TYPES.stream().noneMatch(type -> hasType(model, type));
    }

    private static JsonNode resolve(JsonNode root, JsonNode schema) {
        JsonNode current = schema;
        for (int depth = 0; depth < MAX_REF_DEPTH && current.has("$ref"); depth++) {
            String ref = current.get("$ref").asText();
            if (!ref.startsWith(SCHEMA_REF_PREFIX)) {
                return MissingNode.getInstance();
            }
            current = root.at(ref.substring(1));
        }
        return current.has("$ref") ? MissingNode.getInstance() : current;
    }

    private static boolean hasType(JsonNode schema, String type) {
        JsonNode typeNode = schema.path("type");
        if (typeNode.isTextual()) {
            return type.equals(typeNode.asText());
        }
        for (JsonNode candidate : typeNode) {
            if (type.equals(candidate.asText())) {
                return true;
            }
        }
        return false;
    }
}
