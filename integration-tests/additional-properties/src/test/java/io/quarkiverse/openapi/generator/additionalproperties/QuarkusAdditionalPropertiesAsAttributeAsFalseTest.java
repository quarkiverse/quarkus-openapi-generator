package io.quarkiverse.openapi.generator.additionalproperties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;

import jakarta.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.no_additional_properties_as_attr_yaml.model.Priority;
import org.openapi.quarkus.no_additional_properties_as_attr_yaml.model.PriorityValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class QuarkusAdditionalPropertiesAsAttributeAsFalseTest {

    @Inject
    ObjectMapper objectMapper;

    @Test
    void when_additional_properties_as_attribute_is_false_should_extends_a_map() {
        assertThat(org.openapi.quarkus.no_additional_properties_as_attr_yaml.model.Priority.class)
                .hasSuperclass(HashMap.class);
    }

    @Test
    void when_additional_properties_as_attribute_is_false_should_not_have_additional_properties() {
        assertThat(org.openapi.quarkus.no_additional_properties_as_attr_yaml.model.Priority.class)
                .hasOnlyDeclaredFields("name");
    }

    @Test
    void test_jackson_serialization_issue() throws JsonProcessingException {
        // arrange
        Priority priority = new Priority();
        priority.setName("name");
        PriorityValue priorityValue = new PriorityValue().code(1).text("text");
        priority.put("value", priorityValue);

        // act
        String json = objectMapper.writeValueAsString(priority);

        // assert
        Assertions.assertThat(json).isNotEqualTo("{\"name\":\"name\",\"value\":{\"code\":1,\"text\":\"text\"}}");
    }

    @Test
    void test_jackson_deserialization_issue() {
        // assert
        String json = "{\"name\":\"name\",\"value\":{\"code\":1,\"text\":\"text\"}}";

        Assertions.assertThatThrownBy(() -> {
            objectMapper.readValue(json, Priority.class);
        });
    }
}
