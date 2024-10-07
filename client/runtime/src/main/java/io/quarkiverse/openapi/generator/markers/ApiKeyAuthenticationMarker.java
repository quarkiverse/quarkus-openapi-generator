package io.quarkiverse.openapi.generator.markers;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.quarkiverse.openapi.generator.providers.ApiKeyIn;

@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ApiKeyAuthenticationMarker.AuthenticationMarkers.class)
public @interface ApiKeyAuthenticationMarker {

    String name();

    String openApiSpecId();

    ApiKeyIn apiKeyIn();

    String apiKeyName();

    @Target(TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface AuthenticationMarkers {
        ApiKeyAuthenticationMarker[] value();
    }

}
