package io.quarkiverse.openapi.generator.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

import org.acme.openapi.api.DoNotGeneratePartFilenameApi;
import org.acme.openapi.api.GeneratePartFilenameApi;
import org.acme.openapi.api.GlobalGeneratePartFilenameApi;
import org.acme.openapi.api.PartFilenameValueApi;
import org.acme.openapi.api.PartFilenameValueSuffixApi;
import org.jboss.resteasy.annotations.providers.multipart.PartFilename;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PartFilenameTest {

    private static final String PROFILE_IMAGE = "profileImage";

    private Field getProfileImageField(Class<?> clazz) throws NoSuchFieldException {
        return clazz.getField(PROFILE_IMAGE);
    }

    private void testOtherAnnotations(Field field) {
        var formParam = field.getAnnotation(FormParam.class);
        assertNotNull(formParam);
        assertEquals(PROFILE_IMAGE, formParam.value());

        var partType = field.getAnnotation(PartType.class);
        assertNotNull(partType);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, partType.value());
    }

    @Test
    void testGlobalGeneratePartFilename() throws NoSuchFieldException {
        var field = getProfileImageField(
                GlobalGeneratePartFilenameApi.PostUserProfileDataMultipartForm.class);

        this.testOtherAnnotations(field);

        var partFilename = field.getAnnotation(PartFilename.class);
        assertNull(partFilename);
    }

    @Test
    void testGeneratePartFilename() throws NoSuchFieldException {
        var field = getProfileImageField(
                GeneratePartFilenameApi.PostUserProfileDataMultipartForm.class);

        this.testOtherAnnotations(field);

        var partFilename = field.getAnnotation(PartFilename.class);
        assertNotNull(partFilename);
        assertEquals(PROFILE_IMAGE + "File", partFilename.value());
    }

    @Test
    void testDoNotGeneratePartFilename() throws NoSuchFieldException {
        var field = getProfileImageField(
                DoNotGeneratePartFilenameApi.PostUserProfileDataMultipartForm.class);

        this.testOtherAnnotations(field);

        var partFilename = field.getAnnotation(PartFilename.class);
        assertNull(partFilename);
    }

    @Test
    void testPartFilenameValue() throws NoSuchFieldException {
        var field = getProfileImageField(
                PartFilenameValueApi.PostUserProfileDataMultipartForm.class);

        this.testOtherAnnotations(field);

        var partFilename = field.getAnnotation(PartFilename.class);
        assertNotNull(partFilename);
        assertEquals("test.pdf", partFilename.value());
    }

    @Test
    void testPartFilenameValueSuffix() throws NoSuchFieldException {
        var field = getProfileImageField(
                PartFilenameValueSuffixApi.PostUserProfileDataMultipartForm.class);

        this.testOtherAnnotations(field);

        var partFilename = field.getAnnotation(PartFilename.class);
        assertNotNull(partFilename);
        assertEquals(PROFILE_IMAGE + ".pdf", partFilename.value());
    }
}
