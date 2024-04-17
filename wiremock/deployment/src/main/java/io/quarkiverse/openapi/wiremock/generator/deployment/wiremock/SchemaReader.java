package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock;

import static io.swagger.v3.parser.util.SchemaTypeUtil.OBJECT_TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.quarkiverse.openapi.wiremock.generator.deployment.wrapper.OpenApiWiremockGeneratorWrapper;
import io.swagger.v3.oas.models.media.Schema;

public interface SchemaReader {

    String EMPTY_JSON_OBJECT = "{}";

    static String readObjectExample(Schema<?> schema) {
        try {
            Map<String, Object> map = mapObjectExample(schema);
            return OpenApiWiremockGeneratorWrapper.OBJECT_MAPPER_INSTANCE.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return EMPTY_JSON_OBJECT;
        }
    }

    private static Map<String, Object> mapObjectExample(Schema<?> example) {
        HashMap<String, Object> currentRoot = new HashMap<>();
        Optional.ofNullable(example.getProperties()).orElse(Map.of())
                .forEach((key, schema) -> {
                    if (schema.getType().equals(OBJECT_TYPE)) {
                        currentRoot.put(key, mapObjectExample(schema));
                    } else {
                        currentRoot.put(key, schema.getExample());
                    }
                });
        return currentRoot;
    }
}
