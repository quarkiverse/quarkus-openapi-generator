package io.quarkiverse.openapi.generator.deployment.template;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MediaTypeExtensionsTest {

    @Test
    void pascalCase() {
        assertEquals("ApplicationJson", MediaTypeExtensions.pascalCase("application/json; charset=UTF-8"));
        assertEquals("ApplicationJson", MediaTypeExtensions.pascalCase("application/json"));
        assertEquals("ImageSvgXml", MediaTypeExtensions.pascalCase("image/svg+xml"));
        assertEquals("TextPlain", MediaTypeExtensions.pascalCase("text/plain"));
        assertEquals("", MediaTypeExtensions.pascalCase(""));
        assertEquals("", MediaTypeExtensions.pascalCase(null));
    }
}
