package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.OpenApiGeneratorConfig.RUNTIME_TIME_CONFIG_PREFIX;
import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkiverse.openapi.generator.AuthConfig;

public abstract class AbstractAuthProvider implements AuthProvider {

    private static final String BEARER_WITH_SPACE = "Bearer ";
    private static final String CANONICAL_AUTH_CONFIG_PROPERTY_NAME = "quarkus." + RUNTIME_TIME_CONFIG_PREFIX
            + ".%s.auth.%s.%s";

    private final String openApiSpecId;
    private final String name;
    private final List<OperationAuthInfo> applyToOperations = new ArrayList<>();

    protected AbstractAuthProvider(String name, String openApiSpecId, List<OperationAuthInfo> operations) {
        this.name = name;
        this.openApiSpecId = openApiSpecId;
        this.applyToOperations.addAll(operations);
    }

    protected static String sanitizeBearerToken(String token) {
        if (token != null && token.toLowerCase().startsWith(BEARER_WITH_SPACE.toLowerCase())) {
            return token.substring(BEARER_WITH_SPACE.length());
        }
        return token;
    }

    public String getOpenApiSpecId() {
        return openApiSpecId;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isTokenPropagation() {
        return ConfigProvider.getConfig()
                .getOptionalValue(getCanonicalAuthConfigPropertyName(AuthConfig.TOKEN_PROPAGATION), Boolean.class)
                .orElse(false);
    }

    public String getTokenForPropagation(MultivaluedMap<String, Object> httpHeaders) {
        String headerName = getHeaderName() != null ? getHeaderName() : HttpHeaders.AUTHORIZATION;
        String propagatedHeaderName = propagationHeaderName(getOpenApiSpecId(), getName(), headerName);
        return Objects.toString(httpHeaders.getFirst(propagatedHeaderName));
    }

    public String getHeaderName() {
        return ConfigProvider.getConfig()
                .getOptionalValue(getCanonicalAuthConfigPropertyName(AuthConfig.HEADER_NAME), String.class).orElse(null);
    }

    @Override
    public List<OperationAuthInfo> operationsToFilter() {
        return applyToOperations;
    }

    public final String getCanonicalAuthConfigPropertyName(String authPropertyName) {
        return String.format(CANONICAL_AUTH_CONFIG_PROPERTY_NAME, getOpenApiSpecId(), getName(), authPropertyName);
    }
}
