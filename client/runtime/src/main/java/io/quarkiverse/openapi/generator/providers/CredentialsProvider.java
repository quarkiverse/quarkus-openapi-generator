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
    String getApiKey(DataInput input);

    /**
     * Gets the username given the OpenAPI definition and security schema
     *
     * @param input the input data available to the method
     * @return the username to use when filtering the request
     */
    String getBasicUsername(DataInput input);

    /**
     * Gets the password given the OpenAPI definition and security schema
     *
     * @param input the input data available to the method
     * @return the password to use when filtering the request
     */
    String getBasicPassword(DataInput input);

    /**
     * Gets the Bearer Token given the OpenAPI definition and security schema
     *
     * @param input the input data available to the method
     * @return the Bearer Token to use when filtering the request
     */
    String getBearerToken(DataInput input);

    /**
     * Gets the OAuth2 Bearer Token given the OpenAPI definition and security schema
     *
     * @param input the input data available to the method
     * @return the Bearer Token to use when filtering the request
     */
    String getOauth2BearerToken(DataInput input);

    class DataInput {
        // requestContext The current request context in which set the authorization header token
        private ClientRequestContext requestContext;
        // openApiSpecId the OpenAPI Spec identification as defined by the OpenAPI Extension
        private String openApiSpecId;
        // authName The security schema for this Bearer Token definition
        private String authName;

        public DataInput(ClientRequestContext requestContext, String openApiSpecId, String authName) {
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

        public static DataInputBuilder builder() {
            return new DataInputBuilder();
        }

        public static class DataInputBuilder {
            private ClientRequestContext requestContext;
            private String openApiSpecId;
            private String authName;

            public DataInputBuilder requestContext(ClientRequestContext requestContext) {
                this.requestContext = requestContext;
                return this;
            }

            public DataInputBuilder openApiSpecId(String openApiSpecId) {
                this.openApiSpecId = openApiSpecId;
                return this;
            }

            public DataInputBuilder authName(String authName) {
                this.authName = authName;
                return this;
            }

            public CredentialsProvider.DataInput build() {
                return new CredentialsProvider.DataInput(requestContext, openApiSpecId, authName);
            }
        }
    }
}
