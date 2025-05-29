package io.quarkiverse.openapi.generator.it.creds;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.ws.rs.client.ClientRequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;

@Dependent
@Alternative
@Priority(200)
public class CustomCredentialsProvider extends ConfigCredentialsProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCredentialsProvider.class);

    public static String TOKEN = "FIXED_TEST_TOKEN";

    @Override
    public String getBearerToken(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        LOGGER.info("========> getBearerToken");
        return TOKEN;
    }
}