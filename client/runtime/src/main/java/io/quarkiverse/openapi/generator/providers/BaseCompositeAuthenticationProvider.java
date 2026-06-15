package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderNamePrefix;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

    static final String REST_CLIENT_URL_CONFIG_PREFIX = "quarkus.rest-client.";
    static final String REST_CLIENT_URL_CONFIG_SUFFIX = ".url";

    private final List<AuthProvider> authProviders;
    private final String openApiSpecId;
    private final String baseUrlPath;

    public BaseCompositeAuthenticationProvider(String openApiSpecId, List<AuthProvider> authProviders) {
        this.openApiSpecId = openApiSpecId;
        this.authProviders = List.copyOf(authProviders);
        this.baseUrlPath = resolveBaseUrlPath();
    }

    public BaseCompositeAuthenticationProvider(List<AuthProvider> authProviders) {
        this(null, authProviders);
    }

    public final List<AuthProvider> getAuthenticationProviders() {
        return authProviders;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        Set<String> removableHeaderPrefix = new HashSet<>();
        List<Exception> exceptionsDuringFilter = new ArrayList<>();
        boolean applied = false;

        for (AuthProvider authProvider : authProviders) {
            if (authProvider instanceof AbstractAuthProvider) {
                removableHeaderPrefix
                        .add(propagationHeaderNamePrefix(((AbstractAuthProvider) authProvider).getOpenApiSpecId()));
            }
            if (canFilter(authProvider, requestContext)) {
                Optional<Exception> possibleException = tryFilter(authProvider, requestContext);
                if (possibleException.isEmpty()) {
                    applied = true;
                    break;
                }
                exceptionsDuringFilter.add(possibleException.get());
            }
        }

        if (!applied) {
            throwExceptionFrom(exceptionsDuringFilter);
        }

        removeAuthenticationTemporalHeaders(requestContext, removableHeaderPrefix);
    }

    /**
     * Tries to apply the filter of the given provider.
     * As per the OpenAPI spec, only one security requirement needs to be satisfied.
     * See <a href="https://spec.openapis.org/oas/v3.1.0#security-requirement-object">Security Requirement Object</a>:
     * "A declaration of security schemes which can be used for the API operation.
     * The list of values includes alternative security requirement objects that can be used.
     * Only one of the security requirement objects need to be satisfied to authorize a request."
     * Thus, if one provider successfully applied, we stop trying others for this request.
     *
     * @return an empty {@link Optional} if the filter was applied successfully,
     *         or an {@link Optional} containing the exception if the provider failed.
     */
    private Optional<Exception> tryFilter(AuthProvider authProvider, ClientRequestContext requestContext) {
        try {
            authProvider.filter(requestContext);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }

    private static void throwExceptionFrom(List<Exception> exceptionsDuringFilter) throws IOException {
        if (exceptionsDuringFilter.isEmpty()) {
            return;
        }

        // If none of the alternative providers could be applied, re-throw the last exception.
        Exception lastException = exceptionsDuringFilter.get(exceptionsDuringFilter.size() - 1);
        if (lastException instanceof IOException) {
            throw (IOException) lastException;
        } else if (lastException instanceof RuntimeException) {
            throw (RuntimeException) lastException;
        } else {
            throw new RuntimeException(lastException);
        }
    }

    /**
     * It can perform the authentication filter only if this operation requires it (has a security reference).
     * When the REST client base URL contains a path component, that prefix is stripped from the request
     * URI path before matching against the operation paths defined in the OpenAPI spec. This ensures
     * authentication works correctly when the configured base URL path differs from the spec's server
     * URL path (e.g., when using an API gateway stage prefix).
     */
    private boolean canFilter(final AuthProvider authProvider, final ClientRequestContext requestContext) {
        String requestPath = requestContext.getUri().getPath();
        String method = requestContext.getMethod();
        return authProvider.operationsToFilter().stream()
                .anyMatch(o -> {
                    boolean methodMatch = o.getHttpMethod().equals(method);
                    boolean pathMatch = o.matchPath(requestPath);
                    boolean strippedMatch = false;
                    if (methodMatch && !pathMatch) {
                        strippedMatch = matchesWithBaseUrlStripped(o, requestPath, this.baseUrlPath);
                    }
                    return methodMatch && (pathMatch || strippedMatch);
                });
    }

    /**
     * Attempts to match the operation path by stripping the base URL path prefix from the request path.
     * This handles the case where the REST client base URL path differs from the context path
     * embedded in the OpenAPI spec's server URL.
     */
    private boolean matchesWithBaseUrlStripped(OperationAuthInfo op, String requestPath, String baseUrlPath) {
        if (baseUrlPath == null || baseUrlPath.isEmpty() || !requestPath.startsWith(baseUrlPath)) {
            return false;
        }
        int baseUrlLen = baseUrlPath.length();
        if (requestPath.length() > baseUrlLen && requestPath.charAt(baseUrlLen) != '/') {
            return false;
        }
        String strippedPath = requestPath.substring(baseUrlLen);
        if (!strippedPath.startsWith("/")) {
            strippedPath = "/" + strippedPath;
        }
        return op.matchPath(strippedPath);
    }

    /**
     * Resolves the path component of the REST client base URL configured via
     * {@code quarkus.rest-client.<openApiSpecId>.url}.
     * Returns null if the openApiSpecId is not set or the URL cannot be resolved.
     */
    String getBaseUrlPath() {
        return baseUrlPath;
    }

    private String resolveBaseUrlPath() {
        if (openApiSpecId == null) {
            return null;
        }
        Optional<String> urlOpt = ConfigProvider.getConfig()
                .getOptionalValue(REST_CLIENT_URL_CONFIG_PREFIX + openApiSpecId + REST_CLIENT_URL_CONFIG_SUFFIX, String.class);
        return urlOpt.map(this::extractPathFromUrl).orElse(null);
    }

    private String extractPathFromUrl(String url) {
        try {
            URI uri = URI.create(url);
            String path = uri.getPath();
            if (path == null || path.isEmpty() || "/".equals(path)) {
                return null;
            }
            if (path.endsWith("/") && path.length() > 1) {
                path = path.substring(0, path.length() - 1);
            }
            return path;
        } catch (Exception e) {
            return null;
        }
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
