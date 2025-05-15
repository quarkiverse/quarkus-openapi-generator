package io.quarkiverse.openapi.generator.it.auth;

import java.util.Set;

import io.quarkiverse.openapi.generator.it.auth.provider.CustomCredentialsProvider;
import io.quarkus.test.junit.QuarkusTestProfile;

public class TokenTestProfile implements QuarkusTestProfile {
    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return Set.of(CustomCredentialsProvider.class);
    }
}
