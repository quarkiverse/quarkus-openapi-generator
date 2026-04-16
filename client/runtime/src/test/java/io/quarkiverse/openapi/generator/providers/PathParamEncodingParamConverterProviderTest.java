package io.quarkiverse.openapi.generator.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.annotations.EncodedPathParam;
import io.quarkiverse.openapi.generator.annotations.MultiSegmentPathParam;

class PathParamEncodingParamConverterProviderTest {

    private final PathParamEncodingParamConverterProvider provider = new PathParamEncodingParamConverterProvider();

    @Test
    void encodesRawPathSegmentsWithoutDoubleEncodingEscapes() throws Exception {
        Annotation[] annotations = encodedPathParamAnnotations();

        var converter = provider.getConverter(String.class, String.class, annotations);
        assertEquals("mygroup%2Fmyproject%2Fbackend", converter.toString("mygroup/myproject/backend"));
        assertEquals("mygroup%2Fmyproject%2Fbackend", converter.toString("mygroup%2Fmyproject%2Fbackend"));
        assertEquals("space%20and%2Bplus", converter.toString("space and+plus"));
        assertEquals("caf%C3%A9", converter.toString("café"));
    }

    @Test
    void preservesSlashesForMultiSegmentPathParams() throws Exception {
        Annotation[] annotations = multiSegmentPathParamAnnotations();

        var converter = provider.getConverter(String.class, String.class, annotations);
        assertEquals("heads/feature-a", converter.toString("heads/feature-a"));
        assertEquals("heads/team/feature-b", converter.toString("heads/team/feature-b"));
        assertEquals("tags/v1.0.0", converter.toString("tags/v1.0.0"));
        assertEquals("space%20and%2Bplus", converter.toString("space and+plus"));
        assertEquals("caf%C3%A9", converter.toString("café"));
        assertEquals("already%2Fencoded", converter.toString("already%2Fencoded"));
    }

    @Test
    void doesNotApplyToRegularParameters() {
        assertNull(provider.getConverter(String.class, String.class, new Annotation[0]));
    }

    @Test
    void reusesTheSameConverterInstance() throws Exception {
        Annotation[] annotations = encodedPathParamAnnotations();

        var converter = provider.getConverter(String.class, String.class, annotations);
        assertSame(converter, provider.getConverter(String.class, String.class, annotations));
    }

    private Annotation[] encodedPathParamAnnotations() throws Exception {
        Method method = getClass().getDeclaredMethod("sample", String.class);
        return method.getParameters()[0].getAnnotations();
    }

    private Annotation[] multiSegmentPathParamAnnotations() throws Exception {
        Method method = getClass().getDeclaredMethod("multiSegmentSample", String.class);
        return method.getParameters()[0].getAnnotations();
    }

    private void sample(@EncodedPathParam String value) {
        Objects.requireNonNull(value);
    }

    private void multiSegmentSample(@MultiSegmentPathParam @EncodedPathParam String value) {
        Objects.requireNonNull(value);
    }
}
