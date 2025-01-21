package io.quarkiverse.openapi.generator;

import java.util.Map;
import java.util.Optional;

import io.quarkiverse.openapi.generator.providers.ApiKeyAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.BasicAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.BearerAuthenticationProvider;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/**
 * This class represents the runtime authentication related configuration for an individual securityScheme present
 * on an OpenApi spec definition, i.e. the provided files.
 */
@ConfigGroup
public class AuthConfig {

    public static final String TOKEN_PROPAGATION = "token-propagation";
    public static final String HEADER_NAME = "header-name";

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
    @ConfigItem(defaultValue = "false")
    public Optional<Boolean> tokenPropagation;

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
    @ConfigItem
    public Optional<String> headerName;

    /**
     * Configures a particular parameter value to be used by any of the different internal authentication filters
     * that processes the different securityScheme definitions.
     * <p>
     * For example, given a file named petstore.json with a securityScheme named "petstore-basic-auth", that is of
     * http basic authentication type, the following configuration can establish the user and password to be used.
     * must be used.
     * <p>
     * quarkus.openapi-generator.petstore_json.auth.petstore_basic_auth.username=MyUserName
     * quarkus.openapi-generator.petstore_json.auth.petstore_basic_auth.password=MyPassword
     *
     * @see AuthsConfig
     * @see SpecItemConfig
     * @see OpenApiGeneratorConfig
     * @see BasicAuthenticationProvider
     * @see BearerAuthenticationProvider
     * @see ApiKeyAuthenticationProvider
     */
    @ConfigItem(name = ConfigItem.PARENT)
    public Map<String, String> authConfigParams;

    public Optional<Boolean> getTokenPropagation() {
        return tokenPropagation;
    }

    public Optional<String> getHeaderName() {
        return headerName;
    }

    public Optional<String> getConfigParam(String paramName) {
        return Optional.ofNullable(authConfigParams.get(paramName));
    }

    @Override
    public String toString() {
        return "AuthConfig{" +
                "tokenPropagation=" + tokenPropagation +
                ", headerName=" + headerName +
                ", authConfigParams=" + authConfigParams +
                '}';
    }
}
