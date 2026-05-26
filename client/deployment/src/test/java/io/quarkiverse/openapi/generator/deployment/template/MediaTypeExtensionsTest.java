package io.quarkiverse.openapi.generator.deployment.template;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class MediaTypeExtensionsTest {

    @Test
    void pascalCase() {
        assertEquals("ApplicationJson", MediaTypeExtensions.pascalCase("application/json; charset=utf-8; version=1.1"));
        assertEquals("ApplicationJson", MediaTypeExtensions.pascalCase("application/json; charset=UTF-8"));
        assertEquals("ApplicationJson", MediaTypeExtensions.pascalCase("application/*+json"));
        assertEquals("ApplicationJson", MediaTypeExtensions.pascalCase("application/json"));
        assertEquals("ApplicationXWwwFormUrlencoded", MediaTypeExtensions.pascalCase("application/x-www-form-urlencoded"));
        assertEquals("ApplicationVndApiJson", MediaTypeExtensions.pascalCase("application/vnd.api+json"));
        assertEquals("ImageSvgXml", MediaTypeExtensions.pascalCase("image/svg+xml"));
        assertEquals("TextPlain", MediaTypeExtensions.pascalCase("text/plain"));
        assertEquals("", MediaTypeExtensions.pascalCase(""));
        assertEquals("", MediaTypeExtensions.pascalCase(null));
    }

    @Test
    void deduplicateByMediaType() {
        assertThat(MediaTypeExtensions.deduplicateByMediaType(null))
                .isNull();
        assertThat(MediaTypeExtensions.deduplicateByMediaType(List.of()))
                .isEqualTo(List.of());

        final var deduped = MediaTypeExtensions.deduplicateByMediaType(List.of(
                Map.of("mediaType", "application/json"),
                Map.of("mediaType", "application/*+json"),
                Map.of("mediaType", "application/vnd.api+json"),
                Map.of("mediaType", "application/x-www-form-urlencoded")));

        assertThat(deduped)
                .hasSize(3)
                .allMatch(m -> m.containsKey("mediaType"))
                .map(m -> m.get("mediaType"))
                .containsExactlyInAnyOrder("application/json", "application/vnd.api+json", "application/x-www-form-urlencoded");
    }

    @Test
    void filterByPreferredContentTypeReturnsNullWhenInputIsNull() {
        assertThat(MediaTypeExtensions.filterByPreferredContentType(null, "application/json")).isNull();
    }

    @Test
    void filterByPreferredContentTypeReturnsEmptyWhenInputIsEmpty() {
        assertThat(MediaTypeExtensions.filterByPreferredContentType(List.of(), "application/json")).isEmpty();
    }

    @Test
    void filterByPreferredContentTypeReturnsOriginalWhenPreferredIsBlank() {
        var mediaTypes = List.of(
                Map.of("mediaType", "application/json"),
                Map.of("mediaType", "application/xml"));
        assertThat(MediaTypeExtensions.filterByPreferredContentType(mediaTypes, "")).isEqualTo(mediaTypes);
        assertThat(MediaTypeExtensions.filterByPreferredContentType(mediaTypes, null)).isEqualTo(mediaTypes);
    }

    @Test
    void filterByPreferredContentTypeFiltersToSingleMatchingType() {
        var mediaTypes = List.of(
                Map.of("mediaType", "application/json"),
                Map.of("mediaType", "application/xml"));

        var result = MediaTypeExtensions.filterByPreferredContentType(mediaTypes, "application/json");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("mediaType")).isEqualTo("application/json");
    }

    @Test
    void filterByPreferredContentTypeReturnsOriginalWhenPreferredNotInList() {
        var mediaTypes = List.of(
                Map.of("mediaType", "application/json"),
                Map.of("mediaType", "application/xml"));

        assertThat(MediaTypeExtensions.filterByPreferredContentType(mediaTypes, "text/plain")).isEqualTo(mediaTypes);
    }
}
