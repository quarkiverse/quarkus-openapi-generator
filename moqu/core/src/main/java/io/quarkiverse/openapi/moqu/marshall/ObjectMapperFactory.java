package io.quarkiverse.openapi.moqu.marshall;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Responsible for providing a Single of {@link ObjectMapper} instance.
 */
public class ObjectMapperFactory {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getInstance() {
        return objectMapper;
    }
}
