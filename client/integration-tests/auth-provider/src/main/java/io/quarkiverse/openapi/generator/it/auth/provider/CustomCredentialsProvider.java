package io.quarkiverse.openapi.generator.it.auth.provider;

import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Specializes;

import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;
import io.quarkiverse.openapi.generator.providers.CredentialsContext;

@Dependent
@Alternative
@Specializes
@Priority(201)
public class CustomCredentialsProvider extends ConfigCredentialsProvider {
    public CustomCredentialsProvider() {
    }

    @Override
    public Optional<String> getBearerToken(CredentialsContext input) {
        return Optional.of(super.getBearerToken(input).get() + "_TEST");
    }

    @Override
    public Optional<String> getOauth2BearerToken(CredentialsContext input) {
        return Optional.of("KEYCLOAK_ACCESS_TOKEN" + "_TEST");
    }
}
