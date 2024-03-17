package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.media.Schema;

class SchemaReaderTest {
    @Test
    @DisplayName("Should read OpenAPI schema with one level correctly")
    void should_convert_schema_with_one_level_correctly() {
        // given
        Schema<?> name = new Schema<>();
        name.setType("string");
        name.setExample("John Doe");

        Schema<?> user = new Schema<>();
        user.setType("object");
        user.addProperty("name", name);

        // when
        String schema = SchemaReader.readObjectExample(user);

        // then
        Assertions.assertTrue(schema.contains("\"name\":\"John Doe\""));
    }

    @Test
    @DisplayName("Should read OpenAPI schema with two levels correctly")
    void should_read_openapi_schema_with_two_levels_correctly() {
        // given
        Schema<?> name = new Schema<>();
        name.setType("string");
        name.setExample("John Doe");

        Schema<?> id = new Schema<>();
        id.setType("integer");
        id.setExample(10);

        Schema<?> metadata = new Schema<>();
        metadata.setType("object");
        metadata.addProperty("id", id);

        Schema<?> user = new Schema<>();
        user.setType("object");
        user.addProperty("name", name);
        user.addProperty("metadata", metadata);

        // when
        String schema = SchemaReader.readObjectExample(user);

        // then
        Assertions.assertEquals("{\"metadata\":{\"id\":10},\"name\":\"John Doe\"}", schema);
    }
}