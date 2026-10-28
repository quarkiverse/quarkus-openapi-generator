package io.quarkiverse.openapi.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.util.TypeLiteral;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.OpenApiSpec.Literal;
import io.quarkiverse.openapi.generator.providers.ApiKeyAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.ApiKeyIn;
import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkiverse.openapi.generator.providers.BaseCompositeAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.BasicAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.BearerAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.CredentialsProvider;
import io.quarkiverse.openapi.generator.providers.OperationAuthInfo;
import io.quarkus.arc.SyntheticCreationalContext;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class AuthenticationRecorder {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationRecorder.class);

    public Function<SyntheticCreationalContext<BaseCompositeAuthenticationProvider>, BaseCompositeAuthenticationProvider> recordCompositeProvider(
            String openApiSpec) {
        return ctx -> {
            Instance<AuthProvider> authProviderInstances = ctx.getInjectedReference(new TypeLiteral<Instance<AuthProvider>>() {
            }, new Literal(openApiSpec));

            // Each AuthProvider is resolved individually rather than via a single Instance#stream/toList call.
            // A single misconfigured provider (e.g. an OIDC-based one missing its quarkus.oidc-client.* setup)
            // must not prevent every other provider - and therefore every REST client - for this OpenAPI spec
            // from being created.
            List<AuthProvider> providers = new ArrayList<>();
            Iterator<AuthProvider> iterator = authProviderInstances.iterator();
            while (iterator.hasNext()) {
                try {
                    providers.add(iterator.next());
                } catch (RuntimeException e) {
                    LOG.warn(
                            "Skipping an authentication provider for OpenAPI spec '{}' because it failed to initialize. "
                                    + "Requests to operations that rely on it will not be authenticated. Cause: {}",
                            openApiSpec, e.getMessage(), e);
                }
            }
            return new BaseCompositeAuthenticationProvider(openApiSpec, providers);
        };
    }

    public Function<SyntheticCreationalContext<AuthProvider>, AuthProvider> recordApiKeyAuthProvider(
            String name,
            String openApiSpecId,
            ApiKeyIn apiKeyIn,
            String apiKeyName,
            List<OperationAuthInfo> operations) {
        return context -> new ApiKeyAuthenticationProvider(openApiSpecId, name, apiKeyIn,
                apiKeyName, operations, context.getInjectedReference(CredentialsProvider.class));
    }

    public Function<SyntheticCreationalContext<AuthProvider>, AuthProvider> recordBearerAuthProvider(
            String name,
            String scheme,
            String openApiSpecId,
            List<OperationAuthInfo> operations) {
        return context -> new BearerAuthenticationProvider(openApiSpecId, name, scheme,
                operations, context.getInjectedReference(CredentialsProvider.class));
    }

    public Function<SyntheticCreationalContext<AuthProvider>, AuthProvider> recordBasicAuthProvider(
            String name,
            String openApiSpecId,
            List<OperationAuthInfo> operations) {

        return context -> new BasicAuthenticationProvider(openApiSpecId, name,
                operations, context.getInjectedReference(CredentialsProvider.class));

    }

}
