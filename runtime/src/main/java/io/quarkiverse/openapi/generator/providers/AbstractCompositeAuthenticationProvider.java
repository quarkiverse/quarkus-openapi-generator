package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import io.quarkiverse.openapi.generator.CodegenConfig;

/**
 * Composition of supported {@link ClientRequestFilter} defined by a given OpenAPI interface.
 * This class is used as the base class of generated code.
 */
public abstract class AbstractCompositeAuthenticationProvider implements ClientRequestFilter {

    private final List<AuthProvider> authProviders = new ArrayList<>();

    public final void addAuthenticationProvider(final AuthProvider authProvider) {
        this.authProviders.add(authProvider);
    }

    @Override
    public final void filter(ClientRequestContext requestContext) throws IOException {
        for (AuthProvider authProvider : authProviders) {
            if (canFilter(authProvider, requestContext)) {
                authProvider.filter(requestContext);
            }
        }
    }

    /**
     * It can perform the authentication filter only if this operation requires it (has a security reference)
     */
    private boolean canFilter(final AuthProvider authProvider, final ClientRequestContext requestContext) {
        return authProvider.operationsToFilter().stream()
                .anyMatch(o -> o.getHttpMethod().equals(requestContext.getMethod()) &&
                        o.matchPath(requestContext.getUri().getPath()));
    }

    protected static String sanitizeAuthName(String schemeName) {
        return CodegenConfig.getSanitizedSecuritySchemeName(schemeName);
    }
}
