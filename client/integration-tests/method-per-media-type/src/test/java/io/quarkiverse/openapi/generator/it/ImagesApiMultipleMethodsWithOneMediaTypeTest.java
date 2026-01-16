package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ImagesApiMultipleMethodsWithOneMediaTypeTest {
    static Stream<Class<?>> imagesApiClasses() {
        return Stream.of(
                org.acme.json.multiple.media.types.disabled.api.ImagesApi.class,
                org.acme.yaml.multiple.media.types.disabled.api.ImagesApi.class);
    }

    @ParameterizedTest
    @MethodSource("imagesApiClasses")
    void imagesApiIsBeingGenerated(Class<?> imagesApiClass) throws NoSuchMethodException {
        assertImagesApiPerClass(imagesApiClass);
    }

    private static void assertImagesApiPerClass(Class<?> imagesApiClass) throws NoSuchMethodException {
        Method getImage = imagesApiClass.getMethod("getImage", String.class);
        assertThat(getImage.getReturnType()).isEqualTo(File.class);

        Method listImages = imagesApiClass.getMethod("listImages", Integer.class, Integer.class);
        assertThat(listImages.getReturnType()).isEqualTo(List.class);

        Method updateImage = imagesApiClass.getMethod("updateImage", String.class, File.class);
        assertThat(updateImage.getReturnType()).isEqualTo(Response.class);

        Consumes consumes = updateImage.getAnnotation(Consumes.class);
        assertThat(consumes).as("@Consumes must be present on updateImage")
                .isNotNull();
        assertThat(consumes.value())
                .containsExactlyInAnyOrder("image/jpeg", "image/png", "image/gif");
    }
}
