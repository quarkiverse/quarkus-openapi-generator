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
        if (mediaType == null) {
            return "";
        }

        // Remove optional parameters (like ";charset=UTF-8")
        mediaType = mediaType.split(";", 2)[0];
        if (mediaType.isBlank()) {
            return "";
        }

        // Normalize non-alphanumeric characters to a space
        mediaType = mediaType.replaceAll("[^A-Za-z0-9]+", " ");

        return Arrays.stream(mediaType.trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .map(word -> word.substring(0, 1).toUpperCase()
                        + word.substring(1).toLowerCase())
                .collect(joining());
    }
}
