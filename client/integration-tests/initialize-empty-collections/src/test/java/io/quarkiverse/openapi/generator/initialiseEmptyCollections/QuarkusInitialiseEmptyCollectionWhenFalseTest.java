package io.quarkiverse.openapi.generator.initialiseEmptyCollections;

import jakarta.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.quarkus_simple_openapi_null_collections_yaml.model.CloudEvent;
import org.openapi.quarkus.quarkus_simple_openapi_null_collections_yaml.model.Link;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class QuarkusInitialiseEmptyCollectionWhenFalseTest {

    @Inject
    ObjectMapper objectMapper;

    @Test
    void when_initialize_empty_collections_is_false_then_should_not_initialize_collections() {
        var cloudEvent = new CloudEvent();
        Assertions.assertThat(cloudEvent.getAttributeNames()).isNull();
        var link = new Link();
        Assertions.assertThat(link.getRels()).isNull();
    }

    @Test
    void when_initialize_empty_collections_is_false_then_jackson_should_map_collections_correctly()
            throws JsonProcessingException {
        Assertions.assertThat(objectMapper.writeValueAsString(new Link()))
                .isEqualTo("{}");
    }
}
