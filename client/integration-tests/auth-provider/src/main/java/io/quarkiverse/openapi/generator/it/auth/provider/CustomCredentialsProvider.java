package io.quarkiverse.openapi.generator.it.auth.provider;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Specializes;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.providers.AbstractAuthProvider;
import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;

@Dependent
@Alternative
@Specializes
@Priority(10)
public class CustomCredentialsProvider extends ConfigCredentialsProvider {
    static final String BEARER_TOKEN = "bearer-token";

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCredentialsProvider.class);

    public CustomCredentialsProvider() {
    }

    @Override
    public String getBearerToken(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        return ConfigProvider.getConfig()
                .getOptionalValue(
                        AbstractAuthProvider.getCanonicalAuthConfigPropertyName(BEARER_TOKEN, openApiSpecId, authName),
                        String.class)
                .orElse("") + "_TEST";
    }

    @Override
    public String getOauth2BearerToken(ClientRequestContext requestContext) {
        return requestContext.getHeaderString(HttpHeaders.AUTHORIZATION) + "_TEST";
    }
}
