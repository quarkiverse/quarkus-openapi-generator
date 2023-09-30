package io.quarkiverse.openapi.generator.additionalproperties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class QuarkusAdditionalPropertiesAsAttributeAsFalseTest {

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
}
