package io.quarkiverse.openapi.generator;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.util.TypeLiteral;

import io.quarkiverse.openapi.generator.OpenApiSpec.Literal;
import io.quarkiverse.openapi.generator.providers.ApiKeyAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.ApiKeyIn;
import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkiverse.openapi.generator.providers.BasicAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.BearerAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.CompositeAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.OperationAuthInfo;
import io.quarkus.arc.SyntheticCreationalContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class AuthenticationRecorder {

    final OpenApiGeneratorConfig generatorConfig;

    public AuthenticationRecorder(OpenApiGeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    public Function<SyntheticCreationalContext<CompositeAuthenticationProvider>, CompositeAuthenticationProvider> recordCompositeProvider(
            String openApiSpec) {
        return ctx -> {
            List<AuthProvider> providers = ctx.getInjectedReference(new TypeLiteral<Instance<AuthProvider>>() {
            }, new Literal(openApiSpec)).stream().toList();
            return new CompositeAuthenticationProvider(providers);
        };
    }

    public Function<SyntheticCreationalContext<AuthProvider>, AuthProvider> recordApiKeyAuthProvider(
            String name,
            String openApiSpecId,
            ApiKeyIn apiKeyIn,
            String apiKeyName,
            List<OperationAuthInfo> operations) {
        return context -> new ApiKeyAuthenticationProvider(openApiSpecId, name, apiKeyIn, apiKeyName,
                getAuthConfig(generatorConfig, openApiSpecId, name),
                operations);
    }

    public Function<SyntheticCreationalContext<AuthProvider>, AuthProvider> recordBearerAuthProvider(
            String name,
            String scheme,
            String openApiSpecId,
            List<OperationAuthInfo> operations) {
        return context -> new BearerAuthenticationProvider(openApiSpecId, name, scheme,
                getAuthConfig(generatorConfig, openApiSpecId, name),
                operations);
    }

    public Function<SyntheticCreationalContext<AuthProvider>, AuthProvider> recordBasicAuthProvider(
            String name,
            String openApiSpecId,
            List<OperationAuthInfo> operations) {
        return context -> new BasicAuthenticationProvider(openApiSpecId, name,
                getAuthConfig(generatorConfig, openApiSpecId, name), operations);
    }

    public static AuthConfig getAuthConfig(OpenApiGeneratorConfig generatorConfig, String openApiSpecId, String name) {
        return Objects.requireNonNull(generatorConfig, "generatorConfig can't be null.")
                .getItemConfig(openApiSpecId)
                .flatMap(SpecItemConfig::getAuth)
                .flatMap(authsConfig -> authsConfig.getItemConfig(name))
                .orElse(null);
    }
}
