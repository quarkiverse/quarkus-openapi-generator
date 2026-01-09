package io.quarkiverse.openapi.generator.deployment.template;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;

import io.quarkus.qute.TemplateExtension;

public class MediaTypeExtensions {

    private MediaTypeExtensions() {
    }

    /**
     * Converts a media type (e.g. "application/json" or "image/svg+xml")
     * into PascalCase (e.g. "ApplicationJson" or "ImageSvgXml").
     */
    @TemplateExtension
    public static String pascalCase(String mediaType) {
        if (mediaType == null || mediaType.isBlank()) {
            return "";
        }

        // Remove optional parameters (like ";charset=UTF-8") and normalize separators to spaces
        mediaType = mediaType.split(";")[0]
                .replace("/", " ")
                .replace("-", " ")
                .replace("+", " ")
                .replace(".", " ")
                .replace("*", " ");

        return Arrays.stream(mediaType.split("\\s+"))
                .filter(s -> !s.isBlank())
                .map(word -> word.substring(0, 1)
                        .toUpperCase()
                        + word.substring(1)
                                .toLowerCase())
                .collect(joining());
    }
}
