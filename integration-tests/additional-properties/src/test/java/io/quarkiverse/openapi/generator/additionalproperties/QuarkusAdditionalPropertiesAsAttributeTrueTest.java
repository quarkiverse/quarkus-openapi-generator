package io.quarkiverse.openapi.generator.additionalproperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.with_additional_properties_as_attr_yaml.model.Priority;
import org.openapi.quarkus.with_additional_properties_as_attr_yaml.model.PriorityValue;

@QuarkusTest
public class QuarkusAdditionalPropertiesAsAttributeTrueTest {

    @Inject
    ObjectMapper mapper;

    @Test
    void when_additional_properties_is_true_then_should_create_additional_properties_attribute() {
        AssertionsForClassTypes.assertThat(org.openapi.quarkus.with_additional_properties_as_attr_yaml.model.Priority.class)
                .hasOnlyDeclaredFields("name", "additionalProperties");
    }

    @Test
    void when_additional_properties_is_true_then_jackson_should_maps_the_additional_properties_correctly() throws JsonProcessingException {
        // arrange
        Priority priority = new org.openapi.quarkus.with_additional_properties_as_attr_yaml.model.Priority();
        priority.setName("name");
        PriorityValue priorityValue = new PriorityValue();
        PriorityValue value = priorityValue.code(1).text("text");
        priority.putAdditionalProperty("value", value);

        // act
        String json = mapper.writeValueAsString(priority);

        // assert
        Assertions.assertThat(json).isEqualTo("{\"name\":\"name\",\"value\":{\"code\":1,\"text\":\"text\"}}");
    }

    @Test
    void when_additional_properties_is_true_then_should_create_to_string_with_additional_properties() throws JsonProcessingException {
        // arrange
        Priority priority = new org.openapi.quarkus.with_additional_properties_as_attr_yaml.model.Priority();
        priority.setName("name");
        PriorityValue priorityValue = new PriorityValue();
        PriorityValue value = priorityValue.code(1).text("text");
        priority.putAdditionalProperty("value", value);

        // act
        String toStringOutput = priority.toString();

        // assert
        Assertions.assertThat(toStringOutput).contains("additionalProperties: ");
    }


}
