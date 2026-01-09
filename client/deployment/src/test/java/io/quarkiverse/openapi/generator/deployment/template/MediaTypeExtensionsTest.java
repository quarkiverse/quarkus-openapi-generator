package io.quarkiverse.openapi.generator.deployment.template;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MediaTypeExtensionsTest {

    @Test
    void camelCase() {
        assertEquals("ApplicationJson", MediaTypeExtensions.camelCase("application/json; charset=UTF-8"));
        assertEquals("ApplicationJson", MediaTypeExtensions.camelCase("application/json"));
        assertEquals("ImageSvgXml", MediaTypeExtensions.camelCase("image/svg+xml"));
        assertEquals("TextPlain", MediaTypeExtensions.camelCase("text/plain"));
        assertEquals("", MediaTypeExtensions.camelCase(""));
        assertEquals("", MediaTypeExtensions.camelCase(null));
    }
}
