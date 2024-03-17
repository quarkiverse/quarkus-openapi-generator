package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock;

import static io.swagger.v3.parser.util.SchemaTypeUtil.OBJECT_TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.quarkiverse.openapi.wiremock.generator.deployment.wrapper.OpenApiWiremockGeneratorWrapper;
import io.swagger.v3.oas.models.media.Schema;

public class SchemaReader {

    public static final String EMPTY_JSON_OBJECT = "{}";

    public static String readObjectExample(Schema<?> schema) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            mapObjectExample(map, schema);
            return OpenApiWiremockGeneratorWrapper.OBJECT_MAPPER_INSTANCE.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return EMPTY_JSON_OBJECT;
        }
    }

    private static Map<String, Object> mapObjectExample(Map<String, Object> root, Schema<?> example) {
        Optional.ofNullable(example.getProperties()).orElse(Map.of())
                .forEach((key, schema) -> {
                    if (schema.getType().equals(OBJECT_TYPE)) {
                        root.put(key, mapObjectExample(new HashMap<>(), schema));
                    } else {
                        root.put(key, schema.getExample());
                    }
                });
        return root;
    }
}