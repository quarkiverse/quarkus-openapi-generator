package io.quarkiverse.openapi.generator.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a generated path parameter so the client can safely encode path segments.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface EncodedPathParam {
}
