package io.quarkiverse.openapi.generator.providers;

import jakarta.ws.rs.client.ClientRequestContext;

/**
 * Provider for security credentials. Clients can implement this interface to control how to provide security credentials in
 * runtime.
 * Annotate your bean with @RequestScope (or @Dependant) and @Priority(1).
 */
public interface CredentialsProvider {

    /**
     * Gets the API Key given the OpenAPI definition and security schema
     *
     * @param input the input data available to the method
     * @return the API Key to use when filtering the request
     */
    String getApiKey(CredentialsContext input);

    /**
     * Gets the username given the OpenAPI definition and security schema
     *
     * @param input the input data available to the method
     * @return the username to use when filtering the request
     */
    String getBasicUsername(CredentialsContext input);

    /**
     * Gets the password given the OpenAPI definition and security schema
     *
     * @param input the input data available to the method
     * @return the password to use when filtering the request
     */
    String getBasicPassword(CredentialsContext input);

    /**
     * Gets the Bearer Token given the OpenAPI definition and security schema
     *
     * @param input the input data available to the method
     * @return the Bearer Token to use when filtering the request
     */
    String getBearerToken(CredentialsContext input);

    /**
     * Gets the OAuth2 Bearer Token given the OpenAPI definition and security schema
     *
     * @param input the input data available to the method
     * @return the Bearer Token to use when filtering the request
     */
    String getOauth2BearerToken(CredentialsContext input);

    class CredentialsContext {
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
}
