package io.quarkiverse.openapi.generator.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a generated path parameter that may span multiple URL segments.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface MultiSegmentPathParam {
}
