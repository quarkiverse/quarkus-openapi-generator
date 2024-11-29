package io.quarkiverse.openapi.moqu.marshall;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Responsible for providing a Single of {@link ObjectMapper} instance.
 */
public class ObjectMapperFactory {

    private static ObjectMapper objectMapper;

    public static ObjectMapper getInstance() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }
}
