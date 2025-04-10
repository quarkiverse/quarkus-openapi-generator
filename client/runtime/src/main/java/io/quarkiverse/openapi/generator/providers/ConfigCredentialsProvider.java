package io.quarkiverse.openapi.generator.providers;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.ws.rs.client.ClientRequestContext;

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
    public String getApiKey(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        final String key = ConfigProvider.getConfig()
                .getOptionalValue(AbstractAuthProvider.getCanonicalAuthConfigPropertyName(API_KEY, openApiSpecId, authName),
                        String.class)
                .orElse("");
        if (key.isEmpty()) {
            LOGGER.warn("configured {} property (see application.properties) is empty. hint: configure it.",
                    AbstractAuthProvider.getCanonicalAuthConfigPropertyName(API_KEY, openApiSpecId, authName));
        }
        return key;
    }

    @Override
    public String getBasicUsername(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        return ConfigProvider.getConfig()
                .getOptionalValue(AbstractAuthProvider.getCanonicalAuthConfigPropertyName(USER_NAME, openApiSpecId, authName),
                        String.class)
                .orElse("");
    }

    @Override
    public String getBasicPassword(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        return ConfigProvider.getConfig()
                .getOptionalValue(AbstractAuthProvider.getCanonicalAuthConfigPropertyName(PASSWORD, openApiSpecId, authName),
                        String.class)
                .orElse("");
    }

    @Override
    public String getBearerToken(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        return ConfigProvider.getConfig()
                .getOptionalValue(
                        AbstractAuthProvider.getCanonicalAuthConfigPropertyName(BEARER_TOKEN, openApiSpecId, authName),
                        String.class)
                .orElse("");
    }

}
