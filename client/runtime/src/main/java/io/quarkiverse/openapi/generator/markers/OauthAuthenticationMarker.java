package io.quarkiverse.openapi.generator.markers;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(OauthAuthenticationMarker.AuthenticationMarkers.class)
public @interface OauthAuthenticationMarker {

    String name();

    String openApiSpecId();

    @Target(TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface AuthenticationMarkers {
        OauthAuthenticationMarker[] value();
    }

}
