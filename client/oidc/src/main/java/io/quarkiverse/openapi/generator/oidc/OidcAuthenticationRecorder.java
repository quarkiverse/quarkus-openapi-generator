package io.quarkiverse.openapi.generator.oidc;

import java.util.List;
import java.util.function.Function;

import io.quarkiverse.openapi.generator.AuthenticationRecorder;
import io.quarkiverse.openapi.generator.OidcClient;
import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkiverse.openapi.generator.oidc.providers.OAuth2AuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkiverse.openapi.generator.providers.OperationAuthInfo;
import io.quarkus.arc.SyntheticCreationalContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class OidcAuthenticationRecorder {
    private final OpenApiGeneratorConfig generatorConfig;

    public OidcAuthenticationRecorder(OpenApiGeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    public Function<SyntheticCreationalContext<AuthProvider>, AuthProvider> recordOauthAuthProvider(
            String name,
            String openApiSpecId,
            List<OperationAuthInfo> operations) {
        return context -> new OAuth2AuthenticationProvider(
                AuthenticationRecorder.getAuthConfig(generatorConfig, openApiSpecId, name), name, openApiSpecId,
                context.getInjectedReference(OAuth2AuthenticationProvider.OidcClientRequestFilterDelegate.class,
                        new OidcClient.Literal(name)),
                operations);
    }
}
