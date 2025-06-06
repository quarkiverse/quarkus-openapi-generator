package io.quarkiverse.openapi.generator.it.auth.provider;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Specializes;

import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;
import io.quarkiverse.openapi.generator.providers.CredentialsContext;

@Dependent
@Alternative
@Specializes
@Priority(200)
public class CustomCredentialsProvider extends ConfigCredentialsProvider {
    public CustomCredentialsProvider() {
    }

    @Override
    public String getBearerToken(CredentialsContext input) {
        return super.getBearerToken(input) + "_TEST";
    }

    @Override
    public String getOauth2BearerToken(CredentialsContext input) {
        return super.getOauth2BearerToken(input) + "_TEST";
    }
}
