package io.quarkiverse.openapi.generator.oidc;

import java.util.List;
import java.util.function.Function;

import io.quarkiverse.openapi.generator.OidcClient;
import io.quarkiverse.openapi.generator.oidc.providers.OAuth2AuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkiverse.openapi.generator.providers.CredentialsProvider;
import io.quarkiverse.openapi.generator.providers.OperationAuthInfo;
import io.quarkus.arc.SyntheticCreationalContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class OidcAuthenticationRecorder {

    public Function<SyntheticCreationalContext<AuthProvider>, AuthProvider> recordOauthAuthProvider(
            String name,
            String openApiSpecId,
            List<OperationAuthInfo> operations) {
        return context -> new OAuth2AuthenticationProvider(name, openApiSpecId,
                context.getInjectedReference(OAuth2AuthenticationProvider.OidcClientRequestFilterDelegate.class,
                        new OidcClient.Literal(name)),
                operations,
                context.getInjectedReference(CredentialsProvider.class));
    }
}
