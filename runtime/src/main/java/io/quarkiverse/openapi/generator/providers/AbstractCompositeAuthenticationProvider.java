package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderNamePrefix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;

/**
 * Composition of supported {@link ClientRequestFilter} defined by a given OpenAPI interface.
 * This class is used as the base class of generated code.
 */
public abstract class AbstractCompositeAuthenticationProvider implements ClientRequestFilter {

    private final List<AuthProvider> authProviders = new ArrayList<>();

    public final void addAuthenticationProvider(final AuthProvider authProvider) {
        this.authProviders.add(authProvider);
    }

    public final List<AuthProvider> getAuthenticationProviders() {
        return authProviders;
    }

    @Override
    public final void filter(ClientRequestContext requestContext) throws IOException {
        Set<String> removableHeaderPrefix = new HashSet<>();
        for (AuthProvider authProvider : authProviders) {
            if (authProvider instanceof AbstractAuthProvider) {
                removableHeaderPrefix
                        .add(propagationHeaderNamePrefix(((AbstractAuthProvider) authProvider).getOpenApiSpecId()));
            }
            if (canFilter(authProvider, requestContext)) {
                authProvider.filter(requestContext);
            }
        }
        removeAuthenticationTemporalHeaders(requestContext, removableHeaderPrefix);
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
        return OpenApiGeneratorConfig.getSanitizedSecuritySchemeName(schemeName);
    }

    /**
     * Remove the headers introduced by the AuthenticationHeadersFactory for token propagation purposes.
     */
    private void removeAuthenticationTemporalHeaders(ClientRequestContext requestContext, Set<String> removableHeaderPrefix) {
        Set<String> headersToRemove = new HashSet<>();
        requestContext.getHeaders().keySet().forEach(headerName -> {
            boolean remove = removableHeaderPrefix.stream()
                    .anyMatch(headerName::startsWith);
            if (remove) {
                headersToRemove.add(headerName);
            }
        });
        headersToRemove.forEach(headerToRemove -> requestContext.getHeaders().remove(headerToRemove));
    }
}
