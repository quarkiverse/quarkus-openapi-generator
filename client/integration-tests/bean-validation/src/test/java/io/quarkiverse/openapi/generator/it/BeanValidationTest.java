package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;

import org.junit.jupiter.api.Test;
import org.openapi.quarkus.bean_validation_false_yaml.api.UnvalidatedEndpointApi;
import org.openapi.quarkus.bean_validation_false_yaml.model.UnvalidatedObject;
import org.openapi.quarkus.bean_validation_true_yaml.api.ValidatedEndpointApi;
import org.openapi.quarkus.bean_validation_true_yaml.model.ValidatedObject;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class BeanValidationTest {

    @Test
    void testValidationAnnotationsAreInPlaceApi() {
        Method method = ValidatedEndpointApi.class.getMethods()[0];
        Annotation[][] annotations = method.getParameterAnnotations();
        Boolean validationAnnotationExists = Arrays.stream(annotations)
                .allMatch(annotations1 -> Arrays.stream(annotations1)
                        .filter(a -> a.annotationType().equals(Valid.class)).toList()
                        .size() == 1);

        assertThat(validationAnnotationExists).isTrue();
    }

    @Test
    void testValidationAnnotationsAreInPlaceModel() throws Exception {
        Field id = ValidatedObject.class.getDeclaredField("id");
        Field name = ValidatedObject.class.getDeclaredField("name");

        assertThat(id.isAnnotationPresent(Max.class)).isTrue();
        assertThat(name.isAnnotationPresent(Pattern.class)).isTrue();
    }

    @Test
    void testValidationAnnotationsAreSkippedApi() {
        Method method = UnvalidatedEndpointApi.class.getMethods()[0];
        Annotation[][] annotations = method.getParameterAnnotations();
        Boolean validationAnnotationExists = Arrays.stream(annotations)
                .allMatch(annotations1 -> Arrays.stream(annotations1)
                        .filter(a -> a.annotationType().equals(Valid.class)).toList()
                        .isEmpty());

        assertThat(validationAnnotationExists).isTrue();
    }

    @Test
    void testValidationAnnotationsAreSkippedModel() throws Exception {
        Field id = UnvalidatedObject.class.getDeclaredField("id");
        Field name = UnvalidatedObject.class.getDeclaredField("name");

        assertThat(id.isAnnotationPresent(Max.class)).isFalse();
        assertThat(name.isAnnotationPresent(Pattern.class)).isFalse();
    }
}
