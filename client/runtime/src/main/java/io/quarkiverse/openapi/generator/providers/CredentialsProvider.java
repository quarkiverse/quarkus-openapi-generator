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
     * @param openApiSpecId the OpenAPI Spec identification as defined by the OpenAPI Extension
     * @param authName The security schema for this API Key definition
     * @return the API Key to use when filtering the request
     */
    String getApiKey(ClientRequestContext requestContext, String openApiSpecId, String authName);

    /**
     * Gets the username given the OpenAPI definition and security schema
     *
     * @param openApiSpecId the OpenAPI Spec identification as defined by the OpenAPI Extension
     * @param authName The security schema for this Basic Authorization definition
     * @return the username to use when filtering the request
     */
    String getBasicUsername(ClientRequestContext requestContext, String openApiSpecId, String authName);

    /**
     * Gets the password given the OpenAPI definition and security schema
     *
     * @param openApiSpecId the OpenAPI Spec identification as defined by the OpenAPI Extension
     * @param authName The security schema for this Basic Authorization definition
     * @return the password to use when filtering the request
     */
    String getBasicPassword(ClientRequestContext requestContext, String openApiSpecId, String authName);

    /**
     * Gets the Bearer Token given the OpenAPI definition and security schema
     *
     * @param openApiSpecId the OpenAPI Spec identification as defined by the OpenAPI Extension
     * @param authName The security schema for this Bearer Token definition
     * @return the Bearer Token to use when filtering the request
     */
    String getBearerToken(ClientRequestContext requestContext, String openApiSpecId, String authName);
}
