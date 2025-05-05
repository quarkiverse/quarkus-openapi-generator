package io.quarkiverse.openapi.generator.initialiseEmptyCollections;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import jakarta.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.quarkus_simple_openapi_empty_collection_yaml.model.CloudEvent;
import org.openapi.quarkus.quarkus_simple_openapi_empty_collection_yaml.model.Link;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class QuarkusInitialiseEmptyCollectionWhenTrueTest {

    @Inject
    ObjectMapper objectMapper;

    @Test
    void when_initialize_empty_collections_is_true_then_should_initialize_collections() {
        var cloudEvent = new CloudEvent();
        Assertions.assertThat(cloudEvent.getAttributeNames()).isNotNull().isEmpty();
        Assertions.assertThat(cloudEvent.getAttributeNames()).isInstanceOf(LinkedHashSet.class);
        var link = new Link();
        Assertions.assertThat(link.getRels()).isNotNull().isEmpty();
        Assertions.assertThat(link.getRels()).isInstanceOf(ArrayList.class);
    }

    @Test
    void when_initialize_empty_collections_is_true_then_jackson_should_map_collections_correctly()
            throws JsonProcessingException {
        Assertions.assertThat(objectMapper.writeValueAsString(new Link()))
                .isEqualTo("{\"rels\":[],\"params\":{}}");
    }
}
