package io.quarkiverse.openapi.generator.it.creds;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

import org.junit.jupiter.api.Test;

class CustomCredentialsProviderMetadataTest {

    @Test
    void shouldBeDeclaredAsAnAlternativeOverride() throws Exception {
        assertThat(CustomCredentialsProvider.class.isAnnotationPresent(Dependent.class)).isTrue();
        assertThat(CustomCredentialsProvider.class.isAnnotationPresent(Alternative.class)).isTrue();

        Priority priority = CustomCredentialsProvider.class.getAnnotation(Priority.class);
        assertThat(priority).isNotNull();
        assertThat(priority.value()).isEqualTo(200);

        Constructor<CustomCredentialsProvider> constructor = CustomCredentialsProvider.class.getDeclaredConstructor();
        assertThat(Modifier.isPublic(constructor.getModifiers())).isTrue();
    }
}
