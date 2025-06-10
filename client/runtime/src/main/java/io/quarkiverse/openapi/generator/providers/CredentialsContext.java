package io.quarkiverse.openapi.generator.providers;

import jakarta.ws.rs.client.ClientRequestContext;

public class CredentialsContext {
    // requestContext The current request context in which set the authorization header token
    private ClientRequestContext requestContext;
    // openApiSpecId the OpenAPI Spec identification as defined by the OpenAPI Extension
    private String openApiSpecId;
    // authName The security schema for this Bearer Token definition
    private String authName;

    public CredentialsContext(ClientRequestContext requestContext, String openApiSpecId, String authName) {
        this.requestContext = requestContext;
        this.openApiSpecId = openApiSpecId;
        this.authName = authName;
    }

    public ClientRequestContext getRequestContext() {
        return requestContext;
    }

    public String getOpenApiSpecId() {
        return openApiSpecId;
    }

    public String getAuthName() {
        return authName;
    }

    public static CredentialsContextBuilder builder() {
        return new CredentialsContextBuilder();
    }

    public static class CredentialsContextBuilder {
        private ClientRequestContext requestContext;
        private String openApiSpecId;
        private String authName;

        public CredentialsContextBuilder requestContext(ClientRequestContext requestContext) {
            this.requestContext = requestContext;
            return this;
        }

        public CredentialsContextBuilder openApiSpecId(String openApiSpecId) {
            this.openApiSpecId = openApiSpecId;
            return this;
        }

        public CredentialsContextBuilder authName(String authName) {
            this.authName = authName;
            return this;
        }

        public CredentialsContext build() {
            return new CredentialsContext(requestContext, openApiSpecId, authName);
        }
    }
}
