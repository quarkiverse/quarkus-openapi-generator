package io.quarkiverse.openapi.generator.providers;

import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
@Alternative
@Priority(100)
public class ConfigCredentialsProvider implements CredentialsProvider {

    static final String USER_NAME = "username";
    static final String PASSWORD = "password";
    static final String BEARER_TOKEN = "bearer-token";
    static final String API_KEY = "api-key";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCredentialsProvider.class);

    public ConfigCredentialsProvider() {

    }

    @Override
    public Optional<String> getApiKey(CredentialsContext input) {
        final String key = ConfigProvider.getConfig()
                .getOptionalValue(
                        AbstractAuthProvider.getCanonicalAuthConfigPropertyName(API_KEY, input.getOpenApiSpecId(),
                                input.getAuthName()),
                        String.class)
                .orElse("");
        if (key.isEmpty()) {
            LOGGER.warn("configured {} property (see application.properties) is empty. hint: configure it.",
                    AbstractAuthProvider.getCanonicalAuthConfigPropertyName(API_KEY, input.getOpenApiSpecId(),
                            input.getAuthName()));
        }
        return Optional.of(key);
    }

    @Override
    public Optional<String> getBasicUsername(CredentialsContext input) {
        return Optional.of(ConfigProvider.getConfig()
                .getOptionalValue(
                        AbstractAuthProvider.getCanonicalAuthConfigPropertyName(USER_NAME, input.getOpenApiSpecId(),
                                input.getAuthName()),
                        String.class)
                .orElse(""));
    }

    @Override
    public Optional<String> getBasicPassword(CredentialsContext input) {
        return Optional.of(ConfigProvider.getConfig()
                .getOptionalValue(
                        AbstractAuthProvider.getCanonicalAuthConfigPropertyName(PASSWORD, input.getOpenApiSpecId(),
                                input.getAuthName()),
                        String.class)
                .orElse(""));
    }

    @Override
    public Optional<String> getBearerToken(CredentialsContext input) {
        return Optional.of(ConfigProvider.getConfig()
                .getOptionalValue(
                        AbstractAuthProvider.getCanonicalAuthConfigPropertyName(BEARER_TOKEN, input.getOpenApiSpecId(),
                                input.getAuthName()),
                        String.class)
                .orElse(""));
    }

    @Override
    public Optional<String> getOauth2BearerToken(CredentialsContext input) {
        return Optional.empty();
    }
}
