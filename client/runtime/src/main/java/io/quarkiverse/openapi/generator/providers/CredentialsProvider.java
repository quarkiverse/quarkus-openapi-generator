package io.quarkiverse.openapi.generator.providers;

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
}
