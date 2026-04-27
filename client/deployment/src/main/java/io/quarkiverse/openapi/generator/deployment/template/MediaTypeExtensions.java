package io.quarkiverse.openapi.generator.deployment.template;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static List<Map<String, String>> deduplicateByMediaType(List<Map<String, String>> consumes) {
        if (consumes == null || consumes.isEmpty()) {
            return consumes;
        }
        final Map<String, List<Map<String, String>>> mediaTypeGrouping = consumes
                .stream()
                .collect(Collectors.groupingBy(m -> pascalCase(m.get("mediaType"))));

        // just retain the first 'consumes' based on mediaType to avoid duplicate method signatures
        return mediaTypeGrouping.values().stream()
                .map(mediaTypeGroup -> mediaTypeGroup.get(0))
                .collect(Collectors.toList());
    }
}
