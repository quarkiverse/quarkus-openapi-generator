package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.OpenApiGeneratorConfig.RUNTIME_TIME_CONFIG_PREFIX;
import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkiverse.openapi.generator.AuthConfig;

public abstract class AbstractAuthProvider implements AuthProvider {

    CredentialsProvider credentialsProvider;

    private static final String BEARER_WITH_SPACE = "Bearer ";
    private static final String BASIC_WITH_SPACE = "Basic ";

    private static final String CANONICAL_AUTH_CONFIG_PROPERTY_NAME = "quarkus." + RUNTIME_TIME_CONFIG_PREFIX
            + ".%s.auth.%s.%s";

    private final String openApiSpecId;
    private final String name;
    private final List<OperationAuthInfo> applyToOperations = new ArrayList<>();

    protected AbstractAuthProvider(String name, String openApiSpecId, List<OperationAuthInfo> operations,
            CredentialsProvider credentialsProvider) {
        this.name = name;
        this.openApiSpecId = openApiSpecId;
        this.applyToOperations.addAll(operations);
        this.credentialsProvider = credentialsProvider;
    }

    protected static String sanitizeBearerToken(String token) {
        if (token != null && token.toLowerCase().startsWith(BEARER_WITH_SPACE.toLowerCase())) {
            return token.substring(BEARER_WITH_SPACE.length());
        }
        return token;
    }

    protected static String sanitizeBasicToken(String token) {
        if (token != null && token.toLowerCase().startsWith(BASIC_WITH_SPACE.toLowerCase())) {
            return token.substring(BASIC_WITH_SPACE.length());
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
        return isTokenPropagation(getOpenApiSpecId(), getName());
    }

    public static String getTokenForPropagation(MultivaluedMap<String, Object> httpHeaders, String openApiSpecId,
            String authName) {
        String headerName = getHeaderForPropagation(openApiSpecId, authName);
        String propagatedHeaderName = propagationHeaderName(openApiSpecId, authName, headerName);
        return Objects.toString(httpHeaders.getFirst(propagatedHeaderName), null);
    }

    public String getTokenForPropagation(MultivaluedMap<String, Object> httpHeaders) {
        return getTokenForPropagation(httpHeaders, getOpenApiSpecId(), getName());
    }

    public static String getHeaderForPropagation(String openApiSpecId, String authName) {
        return getHeaderName(openApiSpecId, authName) != null ? getHeaderName(openApiSpecId, authName)
                : HttpHeaders.AUTHORIZATION;
    }

    public String getHeaderName() {
        return ConfigProvider.getConfig()
                .getOptionalValue(getCanonicalAuthConfigPropertyName(AuthConfig.HEADER_NAME), String.class).orElse(null);
    }

    public static String getHeaderName(String openApiSpecId, String authName) {
        return ConfigProvider.getConfig()
                .getOptionalValue(getCanonicalAuthConfigPropertyName(AuthConfig.HEADER_NAME, openApiSpecId, authName),
                        String.class)
                .orElse(null);
    }

    @Override
    public List<OperationAuthInfo> operationsToFilter() {
        return applyToOperations;
    }

    public final String getCanonicalAuthConfigPropertyName(String authPropertyName) {
        return getCanonicalAuthConfigPropertyName(authPropertyName, getOpenApiSpecId(), getName());
    }

    public static String getCanonicalAuthConfigPropertyName(String authPropertyName, String openApiSpecId, String authName) {
        return String.format(CANONICAL_AUTH_CONFIG_PROPERTY_NAME, openApiSpecId, authName, authPropertyName);
    }

    public static boolean isTokenPropagation(String openApiSpecId, String authName) {
        return ConfigProvider.getConfig()
                .getOptionalValue(getCanonicalAuthConfigPropertyName(AuthConfig.TOKEN_PROPAGATION, openApiSpecId, authName),
                        Boolean.class)
                .orElse(false);
    }

    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    protected void addAuthorizationHeader(MultivaluedMap<String, Object> headers, String value) {
        headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(value));
    }

    protected static boolean isEmptyOrBlank(String value) {
        return value == null || value.isBlank();
    }
}
