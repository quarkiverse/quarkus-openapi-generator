package io.quarkiverse.openapi.generator.providers;

import java.util.Base64;

public final class AuthUtils {

    private static final String BASIC_HEADER_PREFIX = "Basic";
    private static final String BEARER_HEADER_PREFIX = "Bearer";

    private AuthUtils() {
    }

    public static String basicAuthAccessToken(final String username, final String password) {
        return String.format("%s %s",
                BASIC_HEADER_PREFIX,
                Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes()));
    }

    public static String authTokenOrBearer(final String scheme, final String token) {
        if (scheme == null) {
            return token;
        }
        // forcing the right case
        if (BEARER_HEADER_PREFIX.equalsIgnoreCase(scheme)) {
            return String.format("%s %s", BEARER_HEADER_PREFIX, token);
        }
        return String.format("%s %s", scheme, token);
    }

}
