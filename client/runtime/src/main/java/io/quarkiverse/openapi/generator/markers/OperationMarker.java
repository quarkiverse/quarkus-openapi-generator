package io.quarkiverse.openapi.generator.markers;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(OperationMarker.OperationMarkers.class)
public @interface OperationMarker {

    String name();

    String openApiSpecId();

    String operationId();

    String path();

    String method();

    /**
     * Array of path parameter names that can match across slashes (multi-segment).
     * Only parameters listed here will use (.*) regex. All others use ([^/]*) for security.
     * Corresponds to OpenAPI x-multi-segment vendor extension.
     */
    String[] multiSegmentParams() default {};

    @Target(METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface OperationMarkers {
        OperationMarker[] value();
    }

}
