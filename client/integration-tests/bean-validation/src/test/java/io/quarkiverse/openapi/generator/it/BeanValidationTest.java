package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
        Annotation[][] annotationsPerParameter = method.getParameterAnnotations();
        Boolean validationAnnotationExists = Arrays.stream(annotationsPerParameter)
                .allMatch(annotations -> Arrays.stream(annotations)
                        .filter(a -> a.annotationType().equals(Valid.class)).toList()
                        .size() == 1);

        assertThat(validationAnnotationExists).isTrue();
    }

    @Test
    void testValidationAnnotationsAreInPlaceModel() throws Exception {
        Field id = ValidatedObject.class.getDeclaredField("id");
        Field name = ValidatedObject.class.getDeclaredField("name");
        Field secondName = ValidatedObject.class.getDeclaredField("secondName");
        Field size = ValidatedObject.class.getDeclaredField("size");

        assertThat(Stream.of(id, name, secondName, size)
                .allMatch(f -> f.isAnnotationPresent(NotNull.class)))
                .isTrue();

        assertThat(id.isAnnotationPresent(Min.class)).isTrue();
        assertThat(id.getAnnotation(Min.class).value()).isEqualTo(1);

        assertThat(id.isAnnotationPresent(Max.class)).isTrue();
        assertThat(id.getAnnotation(Max.class).value()).isEqualTo(100);

        assertThat(name.isAnnotationPresent(Pattern.class)).isTrue();
        assertThat(name.getAnnotation(Pattern.class).regexp()).isEqualTo("[a-zA-Z]*");
        assertThat(name.isAnnotationPresent(Size.List.class)).isTrue();
        assertThat(name.getAnnotationsByType(Size.class)).hasSize(2);
        assertThat(name.getAnnotationsByType(Size.class)[0].min()).isEqualTo(1);
        assertThat(name.getAnnotationsByType(Size.class)[1].max()).isEqualTo(10);

        assertThat(size.isAnnotationPresent(DecimalMin.class)).isTrue();
        assertThat(size.getAnnotation(DecimalMin.class).value()).isEqualTo("1.0");
        assertThat(size.isAnnotationPresent(DecimalMax.class)).isTrue();
        assertThat(size.getAnnotation(DecimalMax.class).value()).isEqualTo("10.0");
    }

    @Test
    void testValidationAnnotationsAreSkippedApi() {
        Method method = UnvalidatedEndpointApi.class.getMethods()[0];
        Annotation[][] annotationsPerParameter = method.getParameterAnnotations();
        Boolean validationAnnotationExists = Arrays.stream(annotationsPerParameter)
                .allMatch(annotations -> Arrays.stream(annotations)
                        .filter(a -> a.annotationType().equals(Valid.class)).toList()
                        .isEmpty());

        assertThat(validationAnnotationExists).isTrue();
    }

    @Test
    void testValidationAnnotationsAreSkippedModel() throws Exception {
        Field id = UnvalidatedObject.class.getDeclaredField("id");
        Field name = UnvalidatedObject.class.getDeclaredField("name");
        Field size = UnvalidatedObject.class.getDeclaredField("size");

        assertThat(Stream.of(id, name, size)
                .noneMatch(f -> f.isAnnotationPresent(NotNull.class))).isTrue();
        assertThat(id.isAnnotationPresent(Min.class)).isFalse();
        assertThat(id.isAnnotationPresent(Max.class)).isFalse();
        assertThat(name.isAnnotationPresent(Pattern.class)).isFalse();
        assertThat(name.isAnnotationPresent(Size.List.class)).isFalse();
        assertThat(size.isAnnotationPresent(DecimalMin.class)).isFalse();
        assertThat(size.isAnnotationPresent(DecimalMax.class)).isFalse();
    }
}
