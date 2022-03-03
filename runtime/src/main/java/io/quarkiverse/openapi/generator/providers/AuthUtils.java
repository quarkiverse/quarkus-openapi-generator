package io.quarkiverse.openapi.generator.providers;

import java.util.Base64;

public final class AuthUtils {

    private static final String BASIC_HEADER_PREFIX = "Basic ";

    private AuthUtils() {
    }

    public static String generateBasicAuthAccessToken(final String username, final String password) {
        return BASIC_HEADER_PREFIX
                + Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes());
    }

}
