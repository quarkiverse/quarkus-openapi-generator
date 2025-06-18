package io.quarkiverse.openapi.generator.providers;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.ws.rs.client.ClientRequestContext;

import org.eclipse.microprofile.config.ConfigProvider;

@Dependent
@Alternative
@Priority(100)
public class ConfigCredentialsProvider implements CredentialsProvider {

    static final String USER_NAME = "username";
    static final String PASSWORD = "password";
    static final String BEARER_TOKEN = "bearer-token";
    static final String API_KEY = "api-key";

    @Override
    public Optional<String> getApiKey(CredentialsContext input) {
        return ConfigProvider.getConfig()
                .getOptionalValue(
                        AbstractAuthProvider.getCanonicalAuthConfigPropertyName(API_KEY, getConfigKey(input),
                                input.getAuthName()),
                        String.class);

    }

    @Override
    public String getBasicUsername(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        return ConfigProvider.getConfig()
                .getOptionalValue(
                        AbstractAuthProvider.getCanonicalAuthConfigPropertyName(USER_NAME, getConfigKey(input),
                                input.getAuthName()),
                        String.class);
    }

    @Override
    public String getBasicPassword(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        return ConfigProvider.getConfig()
                .getOptionalValue(
                        AbstractAuthProvider.getCanonicalAuthConfigPropertyName(PASSWORD, getConfigKey(input),
                                input.getAuthName()),
                        String.class);
    }

    @Override
    public String getBearerToken(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        return ConfigProvider.getConfig()
                .getOptionalValue(
                        AbstractAuthProvider.getCanonicalAuthConfigPropertyName(BEARER_TOKEN, getConfigKey(input),
                                input.getAuthName()),
                        String.class);
    }

    protected String getConfigKey(CredentialsContext input) {
        return input.getOpenApiSpecId();
    }

    @Override
    public Optional<String> getOauth2BearerToken(CredentialsContext input) {
        return Optional.empty();
    }
}
