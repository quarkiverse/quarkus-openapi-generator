package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderNamePrefix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

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
