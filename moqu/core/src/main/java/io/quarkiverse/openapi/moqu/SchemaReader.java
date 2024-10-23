package io.quarkiverse.openapi.moqu;

import static io.swagger.v3.parser.util.SchemaTypeUtil.OBJECT_TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.quarkiverse.openapi.moqu.marshall.ObjectMapperFactory;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Utility class for reading schema examples and converting them into JSON representations.
 * This class provides methods to extract example data from a given schema and serialize it
 * into JSON format.
 */
public class SchemaReader {

    static String EMPTY_JSON_OBJECT = "{}";

    /**
     * Reads the example object from the provided schema and converts it to a JSON string.
     *
     * @param schema the schema from which to extract the example object.
     * @return a JSON string representation of the example object, or an empty JSON object
     *         if an error occurs during processing.
     */
    static String readObjectExample(Schema<?> schema) {
        try {
            Map<String, Object> map = mapObjectExample(schema);
            return ObjectMapperFactory.getInstance().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return EMPTY_JSON_OBJECT;
        }
    }

    /**
     * Recursively maps the properties of the provided schema to a map.
     *
     * @param schema the schema from which to map properties.
     * @return a map representing the example properties of the schema.
     */
    private static Map<String, Object> mapObjectExample(Schema<?> schema) {
        Map<String, Object> currentRoot = new HashMap<>();

        Optional.ofNullable(schema.getProperties())
                .orElse(Map.of())
                .forEach((key, value) -> {
                    if (value.getType().equals(OBJECT_TYPE)) {
                        if (value.getExample() != null) {
                            currentRoot.put(key, value.getExample());
                        } else {
                            currentRoot.put(key, mapObjectExample(value));
                        }
                    } else {
                        currentRoot.put(key, value.getExample());
                    }
                });

        return currentRoot;
    }
}
