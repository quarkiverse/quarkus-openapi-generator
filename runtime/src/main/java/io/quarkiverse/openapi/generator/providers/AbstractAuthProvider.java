package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.OpenApiGeneratorConfig.RUNTIME_TIME_CONFIG_PREFIX;
import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import io.quarkiverse.openapi.generator.AuthConfig;
import io.quarkiverse.openapi.generator.AuthsConfig;
import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkiverse.openapi.generator.SpecItemConfig;

public abstract class AbstractAuthProvider implements AuthProvider {

    private static final String BEARER_WITH_SPACE = "Bearer ";
    private static final String CANONICAL_AUTH_CONFIG_PROPERTY_NAME = "quarkus." +
            RUNTIME_TIME_CONFIG_PREFIX + ".%s.auth.%s.%s";

    private final String openApiSpecId;
    private final String name;
    private final OpenApiGeneratorConfig generatorConfig;
    private AuthConfig authConfig;
    private final List<OperationAuthInfo> applyToOperations = new ArrayList<>();

    protected AbstractAuthProvider(String openApiSpecId, String name, OpenApiGeneratorConfig generatorConfig) {
        this.openApiSpecId = openApiSpecId;
        this.name = name;
        this.generatorConfig = generatorConfig;
        Optional<SpecItemConfig> specItemConfig = generatorConfig.getItemConfig(openApiSpecId);
        if (specItemConfig.isPresent()) {
            Optional<AuthsConfig> authsConfig = specItemConfig.get().getAuth();
            authsConfig.ifPresent(
                    specItemAuthsConfig -> authConfig = specItemAuthsConfig.getItemConfig(name).orElse(null));
        }
    }

    public String getOpenApiSpecId() {
        return openApiSpecId;
    }

    @Override
    public String getName() {
        return name;
    }

    public OpenApiGeneratorConfig getGeneratorConfig() {
        return generatorConfig;
    }

    public boolean isTokenPropagation() {
        return authConfig != null && authConfig.getTokenPropagation().orElse(false);
    }

    public String getTokenForPropagation(MultivaluedMap<String, Object> httpHeaders) {
        String headerName = getHeaderName() != null ? getHeaderName() : HttpHeaders.AUTHORIZATION;
        String propagatedHeaderName = propagationHeaderName(getOpenApiSpecId(), getName(), headerName);
        return Objects.toString(httpHeaders.getFirst(propagatedHeaderName));
    }

    public String getHeaderName() {
        if (authConfig != null) {
            return authConfig.getHeaderName().orElse(null);
        }
        return null;
    }

    @Override
    public List<OperationAuthInfo> operationsToFilter() {
        return applyToOperations;
    }

    @Override
    public AuthProvider addOperation(OperationAuthInfo operationAuthInfo) {
        this.applyToOperations.add(operationAuthInfo);
        return this;
    }

    public String getAuthConfigParam(String paramName, String defaultValue) {
        if (authConfig != null) {
            return authConfig.getConfigParam(paramName).orElse(defaultValue);
        }
        return defaultValue;
    }

    protected static String sanitizeBearerToken(String token) {
        if (token != null && token.toLowerCase().startsWith(BEARER_WITH_SPACE.toLowerCase())) {
            return token.substring(BEARER_WITH_SPACE.length());
        }
        return token;
    }

    protected String getCanonicalAuthConfigPropertyName(String authPropertyName) {
        return String.format(CANONICAL_AUTH_CONFIG_PROPERTY_NAME, getOpenApiSpecId(), getName(), authPropertyName);
    }
}
