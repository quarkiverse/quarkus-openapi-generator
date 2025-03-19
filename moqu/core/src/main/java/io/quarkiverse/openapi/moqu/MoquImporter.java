package io.quarkiverse.openapi.moqu;

/**
 * {@link MoquImporter} aims to convert a specification into a {@link Moqu} model.
 * It provides a method to parse the content, typically from an OpenAPI specification,
 * and generate a corresponding {@link Moqu} instance.
 */
public interface MoquImporter {

    /**
     * Parses the provided OpenAPI content and generates a new {@link Moqu} instance.
     *
     * @param content the OpenAPI content as a string, which will be parsed into a {@link Moqu} model.
     * @return a new {@link Moqu} instance based on the provided content.
     */
    Moqu parse(String content);
}