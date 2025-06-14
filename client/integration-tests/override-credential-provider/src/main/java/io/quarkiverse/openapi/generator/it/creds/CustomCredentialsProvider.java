package io.quarkiverse.openapi.generator.it.creds;

import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;
import io.quarkiverse.openapi.generator.providers.CredentialsContext;

@Dependent
@Alternative
@Priority(200)
public class CustomCredentialsProvider extends ConfigCredentialsProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCredentialsProvider.class);

    public static String TOKEN = "FIXED_TEST_TOKEN";

    @Override
    public Optional<String> getBearerToken(CredentialsContext input) {
        LOGGER.info("========> getBearerToken from CustomCredentialsProvider");
        return Optional.of(TOKEN);
    }
}
