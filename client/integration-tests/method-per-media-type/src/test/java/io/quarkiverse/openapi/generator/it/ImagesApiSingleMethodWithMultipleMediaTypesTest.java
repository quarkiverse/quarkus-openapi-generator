package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ImagesApiSingleMethodWithMultipleMediaTypesTest {
    static Stream<Class<?>> imagesApiClasses() {
        return Stream.of(
                org.acme.json.multiple.media.types.enabled.api.ImagesApi.class,
                org.acme.yaml.multiple.media.types.enabled.api.ImagesApi.class);
    }

    @ParameterizedTest
    @MethodSource("imagesApiClasses")
    void imagesApiIsBeingGeneratedWithMethodPerMediaType(Class<?> imagesApiClass) throws NoSuchMethodException {
        assertImagesApiPerClass(imagesApiClass);
    }

    private static void assertImagesApiPerClass(Class<?> imagesApiClass) throws NoSuchMethodException {
        Method getImage = imagesApiClass.getMethod("getImage", String.class);
        assertThat(getImage.getReturnType()).isEqualTo(File.class);

        Method listImages = imagesApiClass.getMethod("listImages", Integer.class, Integer.class);
        assertThat(listImages.getReturnType()).isEqualTo(List.class);

        assertThat(listImages.getGenericReturnType()
                .getTypeName())
                .contains("java.util.List");

        Map<String, String> expectedMethodSuffixPerMediaType = Map.of(
                "ImageJpeg", "image/jpeg",
                "ImagePng", "image/png",
                "ImageGif", "image/gif");

        List<Method> updateMethods = Arrays.stream(imagesApiClass.getMethods())
                .filter(m -> m.getName()
                        .startsWith("updateImage"))
                .toList();

        assertThat(updateMethods.stream()
                .map(Method::getName)
                .collect(Collectors.toSet()))
                .containsExactlyInAnyOrder("updateImageImageJpeg", "updateImageImagePng", "updateImageImageGif");

        for (Method m : updateMethods) {
            assertThat(m.getReturnType()).isEqualTo(Response.class);
            assertThat(m.getParameterTypes()).containsExactly(String.class, File.class);

            Consumes consumes = m.getAnnotation(Consumes.class);
            assertThat(consumes)
                    .as("@Consumes annotation must be present on %s", m.getName())
                    .isNotNull();
            assertThat(consumes.value())
                    .as("@Consumes annotation must have exactly one media type on %s", m.getName())
                    .hasSize(1);

            String mediaType = consumes.value()[0];

            String suffix = m.getName()
                    .substring("updateImage".length());

            assertThat(expectedMethodSuffixPerMediaType)
                    .as("Unexpected suffix on %s", m.getName())
                    .containsKey(suffix);

            assertThat(mediaType)
                    .as("Consumes media type must match method suffix on %s", m.getName())
                    .isEqualTo(expectedMethodSuffixPerMediaType.get(suffix));
        }
    }
}
