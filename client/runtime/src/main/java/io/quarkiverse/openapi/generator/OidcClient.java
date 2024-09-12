package io.quarkiverse.openapi.generator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

@Qualifier
@Retention(RUNTIME)
@Target({ METHOD, FIELD, PARAMETER, TYPE })
public @interface OidcClient {

    String DEFAULT = "io.quarkiverse.openapi.generator.DEFAULT";

    @Nonbinding
    String name() default DEFAULT;

    final class Literal extends AnnotationLiteral<OidcClient> implements OidcClient {
        public Literal(String name) {
            this.name = name;
        }

        final String name;

        @Override
        public String name() {
            return name;
        }
    }
}
