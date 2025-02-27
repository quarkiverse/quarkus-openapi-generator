package io.quarkiverse.openapi.moqu.wiremock.mapper;

import static io.quarkiverse.openapi.moqu.TestUtils.readContentFromFile;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.moqu.Moqu;
import io.quarkiverse.openapi.moqu.OpenAPIMoquImporter;
import io.quarkiverse.openapi.moqu.wiremock.model.WiremockMapping;

class WiremockMapperTest {

    private final OpenAPIMoquImporter importer = new OpenAPIMoquImporter();
    private final WiremockMapper sut = new WiremockMapper();

    @Test
    @DisplayName("Should map one Wiremock definition")
    void shouldMapOneWiremockDefinition() {
        String content = readContentFromFile("wiremock/mapper/should_map_one_wiremock_definition.yml");

        Moqu moqu = importer.parse(content);

        List<WiremockMapping> definitions = sut.map(moqu);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(definitions).isNotEmpty();
            softly.assertThat(definitions).hasSize(1);
            softly.assertThat(definitions).anySatisfy(definition -> {
                softly.assertThat(definition.request().method()).isEqualTo("GET");
                softly.assertThat(definition.request().url()).isEqualTo("/users/1");
                softly.assertThat(definition.response().body()).isEqualTo("{\"id\": 1, \"name\": \"John Doe\"}");
            });
        });
    }

    @Test
    @DisplayName("Should map two Wiremock definitions")
    void shouldMapTwoWiremockDefinitions() {
        String content = readContentFromFile("wiremock/mapper/should_map_two_wiremock_definition.yml");

        Moqu mock = importer.parse(content);

        List<WiremockMapping> definitions = sut.map(mock);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(definitions).isNotEmpty();
            softly.assertThat(definitions).hasSize(2);
            softly.assertThat(definitions).anySatisfy(definition -> {
                softly.assertThat(definition.request().method()).isEqualTo("GET");
                softly.assertThat(definition.request().url()).isEqualTo("/users/1");
                softly.assertThat(definition.response().body()).isEqualTo("{\"id\": 1, \"name\": \"John Doe\"}");
            });

            softly.assertThat(definitions).anySatisfy(definition -> {
                softly.assertThat(definition.request().method()).isEqualTo("GET");
                softly.assertThat(definition.request().url()).isEqualTo("/users/2");
                softly.assertThat(definition.response().body()).isEqualTo("{\"id\": 2, \"name\": \"Mary Doe\"}");
            });
        });
    }
}
