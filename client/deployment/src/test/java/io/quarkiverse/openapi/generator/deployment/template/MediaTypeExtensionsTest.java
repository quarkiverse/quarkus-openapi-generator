package io.quarkiverse.openapi.generator.deployment.template;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MediaTypeExtensionsTest {

    @Test
    void pascalCase() {
        assertEquals("ApplicationJson", MediaTypeExtensions.pascalCase("application/json; charset=utf-8; version=1.1"));
        assertEquals("ApplicationJson", MediaTypeExtensions.pascalCase("application/json; charset=UTF-8"));
        assertEquals("ApplicationJson", MediaTypeExtensions.pascalCase("application/json"));
        assertEquals("ApplicationXWwwFormUrlencoded", MediaTypeExtensions.pascalCase("application/x-www-form-urlencoded"));
        assertEquals("ApplicationVndApiJson", MediaTypeExtensions.pascalCase("application/vnd.api+json"));
        assertEquals("ImageSvgXml", MediaTypeExtensions.pascalCase("image/svg+xml"));
        assertEquals("TextPlain", MediaTypeExtensions.pascalCase("text/plain"));
        assertEquals("", MediaTypeExtensions.pascalCase(""));
        assertEquals("", MediaTypeExtensions.pascalCase(null));
    }
}
