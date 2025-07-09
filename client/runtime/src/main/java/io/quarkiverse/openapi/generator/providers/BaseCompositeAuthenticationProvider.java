package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderNamePrefix;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;

/**
 * Composition of supported {@link ClientRequestFilter} defined by a given OpenAPI interface.
 * This class is used as the base class of generated code.
 */
public class BaseCompositeAuthenticationProvider implements ClientRequestFilter {

    private final List<AuthProvider> authProviders;

    public BaseCompositeAuthenticationProvider(List<AuthProvider> authProviders) {
        this.authProviders = List.copyOf(authProviders);
    }

    public final List<AuthProvider> getAuthenticationProviders() {
        return authProviders;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
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
        String sanitizedPath = sanitizePath(authProvider, requestContext.getUri());
        return authProvider.operationsToFilter().stream()
                .anyMatch(o -> o.getHttpMethod().equals(requestContext.getMethod()) &&
                        o.matchPath(sanitizedPath));
    }

    /**
     * Calculates the path to realize the matching with the OpenAPI operations, considering that the Quarkus client is
     * configured with a URI that refers to the OpenAPI service endpoint. e.g.:
     *
     * In the OpenAPI document below the service endpoint is: http://https://development.gigantic-server.com/v1,
     *
     * openapi: 3.0.3
     * servers:
     * - url: https://development.gigantic-server.com/v1
     * description: Development server
     * ...
     * paths:
     * /some-operation:
     * post:
     * operationId: SomeOperation
     * ...
     *
     * And thus, the Quarkus client must be configured like this:
     * quarkus.rest-client.example-auth_yaml.url=https://development.gigantic-server.com/v1
     *
     * @param authProvider authentication provider to realize the matching with.
     * @param requestUri the request url to invoke, e.g. https://development.gigantic-server.com/v1/some-operation
     * @return The sanitized path to realize the matching with the OpenApi operations path, following the example above
     *         must be: /some-operation
     */
    protected String sanitizePath(AuthProvider authProvider, URI requestUri) {
        String basePath = getRestClientURLConfig(authProvider).getPath();
        basePath = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;
        String requestPath = requestUri.getPath();
        if (!basePath.isEmpty() && requestPath.startsWith(basePath)) {
            return requestPath.substring(basePath.length());
        }
        return requestPath;
    }

    protected URI getRestClientURLConfig(AuthProvider authProvider) {
        String openApiSpecId = ((AbstractAuthProvider) authProvider).getOpenApiSpecId();
        String restClientProperty = String.format("quarkus.rest-client.%s.url", openApiSpecId);
        return ConfigProvider.getConfig().getOptionalValue(restClientProperty, URI.class)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Required rest client property is not configured: " + restClientProperty));
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
