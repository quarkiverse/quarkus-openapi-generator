package io.quarkiverse.openapi.generator.oidc;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks, by sanitized securityScheme name, which OIDC clients must force a fresh token on their next
 * token fetch.
 * <p/>
 * The generated request-filter and response-filter providers (see {@code compositeAuthenticationProvider.qute}
 * / {@code compositeAuthenticationResponseFilter.qute}) are independent CDI-dependent-scoped object graphs, so
 * the delegate instance that observes a {@code 401} and the delegate instance used for the next request are not
 * guaranteed to be the same Java object. This registry is the single, JVM-wide source of truth that bridges the
 * two, keyed by {@code clientId} (the sanitized securityScheme name).
 */
final class ForceTokenRefreshRegistry {

    private static final Set<String> PENDING = ConcurrentHashMap.newKeySet();

    private ForceTokenRefreshRegistry() {
    }

    static void markForRefresh(String clientId) {
        PENDING.add(clientId);
    }

    static boolean consumeForceRefresh(String clientId) {
        return PENDING.remove(clientId);
    }
}
