package io.quarkiverse.openapi.generator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

@Qualifier
@Retention(RUNTIME)
@Target({ METHOD, FIELD, PARAMETER, TYPE })
public @interface OpenApiSpec {

    String openApiSpecId();

    final class Literal extends AnnotationLiteral<OpenApiSpec> implements OpenApiSpec {
        public Literal(String openApiSpecId) {
            this.openApiSpecId = openApiSpecId;
        }

        final String openApiSpecId;

        @Override
        public String openApiSpecId() {
            return openApiSpecId;
        }
    }
}
