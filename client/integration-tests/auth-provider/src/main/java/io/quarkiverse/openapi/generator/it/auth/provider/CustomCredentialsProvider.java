package io.quarkiverse.openapi.generator.it.auth.provider;

import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;
import io.quarkiverse.openapi.generator.providers.CredentialsContext;

@Dependent
@Alternative
@Priority(201)
public class CustomCredentialsProvider extends ConfigCredentialsProvider {
    public CustomCredentialsProvider() {
    }

    @Override
    public Optional<String> getBearerToken(CredentialsContext input) {
        return Optional.of("BEARER_TOKEN_TEST");
    }

    @Override
    public Optional<String> getOauth2BearerToken(CredentialsContext input) {
        return Optional.of("KEYCLOAK_ACCESS_TOKEN_TEST");
    }
}
