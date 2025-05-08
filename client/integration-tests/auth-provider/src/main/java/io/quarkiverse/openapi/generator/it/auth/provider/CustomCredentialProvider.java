package io.quarkiverse.openapi.generator.it.auth.provider;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.ws.rs.client.ClientRequestContext;

import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;

@RequestScoped
@Alternative
@Priority(10)
public class CustomCredentialProvider extends ConfigCredentialsProvider {
    @Override
    public String getApiKey(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        return super.getApiKey(requestContext, openApiSpecId, authName) + "_TEST";
    }

    @Override
    public String getBasicUsername(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        return super.getBasicUsername(requestContext, openApiSpecId, authName) + "_TEST";
    }

    @Override
    public String getBasicPassword(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        return super.getBasicPassword(requestContext, openApiSpecId, authName) + "_TEST";
    }

    @Override
    public String getBearerToken(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        return super.getBearerToken(requestContext, openApiSpecId, authName) + "_TEST";
    }

    @Override
    public String getOauth2BearerToken(ClientRequestContext requestContext) {
        return (super.getOauth2BearerToken(requestContext) + "_TEST");
    }
}
