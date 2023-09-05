package io.quarkiverse.openapi.generator.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;

import javax.ws.rs.FormParam;

import org.acme.openapi.api.DoNotGeneratePartFilenameApi;
import org.acme.openapi.api.DoNotUseFieldNameInPartFilenameApi;
import org.acme.openapi.api.GeneratePartFilenameApi;
import org.acme.openapi.api.GlobalGeneratePartFilenameApi;
import org.acme.openapi.api.PartFilenameValueApi;
import org.junit.jupiter.api.Test;

public abstract class BasePartFilenameTest {

    protected static final String PROFILE_IMAGE = "profileImage";

    protected abstract void testPartTypeAnnotation(Field field);

    protected abstract void testPartFilenameAnnotation(Field field, boolean present, String value);

    protected void testNonPresentPartFilenameAnnotation(Field field) {
        this.testPartFilenameAnnotation(field, false, null);
    }

    protected Field getProfileImageField(Class<?> clazz) throws NoSuchFieldException {
        return clazz.getField(PROFILE_IMAGE);
    }

    protected void testOtherAnnotations(Field field) {
        // test @FormParam, it is the same for classic and reactive
        var formParam = field.getAnnotation(FormParam.class);
        assertNotNull(formParam);
        assertEquals(PROFILE_IMAGE, formParam.value());

        // test @PartType, it is different for classic and reactive
        testPartTypeAnnotation(field);
    }

    @Test
    void testGlobalGeneratePartFilename() throws NoSuchFieldException {
        var field = getProfileImageField(
                GlobalGeneratePartFilenameApi.PostUserProfileDataMultipartForm.class);

        this.testOtherAnnotations(field);
        this.testNonPresentPartFilenameAnnotation(field);
    }

    @Test
    void testDoNotGeneratePartFilename() throws NoSuchFieldException {
        var field = getProfileImageField(
                DoNotGeneratePartFilenameApi.PostUserProfileDataMultipartForm.class);

        this.testOtherAnnotations(field);
        this.testNonPresentPartFilenameAnnotation(field);
    }

    @Test
    void testGeneratePartFilename() throws NoSuchFieldException {
        var field = getProfileImageField(
                GeneratePartFilenameApi.PostUserProfileDataMultipartForm.class);

        this.testOtherAnnotations(field);
        this.testPartFilenameAnnotation(field, true, PROFILE_IMAGE + "File");
    }

    @Test
    void testPartFilenameValue() throws NoSuchFieldException {
        var field = getProfileImageField(
                PartFilenameValueApi.PostUserProfileDataMultipartForm.class);

        this.testOtherAnnotations(field);
        this.testPartFilenameAnnotation(field, true, PROFILE_IMAGE + ".pdf");
    }

    @Test
    void testDoNotUseFieldNameInPartFilename() throws NoSuchFieldException {
        var field = getProfileImageField(
                DoNotUseFieldNameInPartFilenameApi.PostUserProfileDataMultipartForm.class);

        this.testOtherAnnotations(field);
        this.testPartFilenameAnnotation(field, true, "test.pdf");
    }
}
