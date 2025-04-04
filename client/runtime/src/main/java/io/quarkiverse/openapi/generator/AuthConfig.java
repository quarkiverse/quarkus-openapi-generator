package io.quarkiverse.openapi.generator;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;

/**
 * This class represents the runtime authentication related configuration for an individual securityScheme present
 * on an OpenApi spec definition, i.e. the provided files.
 */
@ConfigGroup
public interface AuthConfig {

    String TOKEN_PROPAGATION = "token-propagation";
    String HEADER_NAME = "header-name";

    /**
     * Enables the authentication token propagation for this particular securityScheme.
     * <p>
     * For example, given a file named petstore.json with a securityScheme named "petstore-auth" the following configuration
     * must be used.
     * <p>
     * quarkus.openapi-generator.petstore_json.auth.petstore_auth.token-propagation=true
     *
     * @see AuthsConfig
     * @see SpecItemConfig
     * @see OpenApiGeneratorConfig
     */
    Optional<Boolean> tokenPropagation();

    /**
     * Configures a particular http header attribute from were to take the security token from when the token propagation
     * is enabled. Use this fine-grained configuration in very particular scenarios.
     * <p>
     * For example, given a file named petstore.json with a securityScheme named "petstore-auth" the following configuration
     * must be used.
     * <p>
     * quarkus.openapi-generator.petstore_json.auth.petstore_auth.header-name=MyParticularHttpHeaderName
     *
     * @see AuthsConfig
     * @see SpecItemConfig
     * @see OpenApiGeneratorConfig
     */
    Optional<String> headerName();

    /**
     * Sets the Basic Authentication username for a given OpenAPI securityScheme.
     * <p/>
     * For example, given a file named petstore.json with a securityScheme named "petstore-basic-auth", that is of
     * http basic authentication type, the following configuration can establish the user to be used.
     * <p/>
     * quarkus.openapi-generator.petstore_json.auth.petstore_basic_auth.username=MyUserName
     *
     * @return the username portion for Basic Authentication
     * @see AuthConfig#password()
     * @see <a href="https://spec.openapis.org/oas/v3.1.0.html#basic-authentication-sample">4.8.27.2.1 Basic Authentication
     *      Sample</a>
     */
    Optional<String> username();

    /**
     * Sets the Basic Authentication password for a given OpenAPI securityScheme.
     * <p/>
     * For example, given a file named petstore.json with a securityScheme named "petstore-basic-auth", that is of
     * http basic authentication type, the following configuration can establish the password to be used.
     * <p/>
     * quarkus.openapi-generator.petstore_json.auth.petstore_basic_auth.password=MyPassword
     * <p/>
     * Ignored if the given securityScheme is not Basic Authentication
     *
     * @return the password portion for Basic Authentication
     * @see AuthConfig#username()
     * @see <a href="https://spec.openapis.org/oas/v3.1.0.html#basic-authentication-sample">4.8.27.2.1 Basic Authentication
     *      Sample</a>
     */
    Optional<String> password();

    /**
     * Sets the Bearer Token for a given OpenAPI securityScheme.
     * <p/>
     * For example, given a file named petstore.json with a securityScheme named "petstore-bearer-auth", that is of
     * bearer authentication type, the following configuration can establish the token to be used.
     * <p/>
     * quarkus.openapi-generator.petstore_json.auth.petstore_bearer_auth.token=1234567890
     * <p/>
     * Ignored if the given securityScheme is not Bearer Token Authentication
     *
     * @return the token
     * @see <a href="https://spec.openapis.org/oas/v3.1.0.html#jwt-bearer-sample">4.8.27.2.3 JWT Bearer Sample</a>
     */
    Optional<String> bearerToken();

    /**
     * Sets the API Key for a given OpenAPI securityScheme.
     * <p/>
     * For example, given a file named petstore.json with a securityScheme named "petstore-apikey-auth", that is of
     * API Key authentication type, the following configuration can establish the API Key to be used.
     * <p/>
     * quarkus.openapi-generator.petstore_json.auth.petstore_apikey_auth.api-key=${MY_SECRET_KEY_IN_AN_ENV_VAR}
     * <p/>
     * Ignored if the given securityScheme is not API Key Authentication
     *
     * @return the token
     * @see <a href="https://spec.openapis.org/oas/v3.1.0.html#api-key-sample">4.8.27.2.2 API Key Samplee</a>
     */
    Optional<String> apiKey();

    /**
     * Only valid for API Key Authentication.
     * <p/>
     * When to add the `Authorization` value to the API Key in the authentication header.
     * <p/>
     * For example, if this property is set to `true`, the API Key will be sent to the server in the header along with
     * `Authorization`:
     * <p/>
     * [source]
     * ---
     * Authentication: Authorization MY-API-KEY
     * ---
     * <p/>
     * If set to `false`, the header should be:
     * <p/>
     * [source]
     * ---
     * Authentication: MY-API-KEY
     * ---
     *
     * @return whether to use the prefix `Authorization` when sending an API Key using headers.
     */
    Optional<Boolean> useAuthorizationHeaderValue();
}
