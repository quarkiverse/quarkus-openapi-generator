package io.quarkiverse.openapi.generator;

import java.util.Map;
import java.util.Optional;

import io.smallrye.config.WithParentName;

public interface AuthsConfig {

    /**
     * Configurations for the individual securitySchemes present on a given OpenApi spec definition file.
     * <p>
     * The key must be any of the sanitized names of the securitySchemes.
     * For example, given a file named petstore.json with a securityScheme named "petstore-auth", we have that the file
     * name is sanitized into the name petstore_json and the security scheme name is sanitized into the name
     * "petstore_auth". And thus, the specific configurations for this security scheme must start with the prefix
     * quarkus.openapi-generator.petstore_json.auth.petstore_auth
     *
     * @see SpecItemConfig
     * @see AuthConfig
     */
    @WithParentName()
    Map<String, AuthConfig> authConfigs();

    default Optional<AuthConfig> getItemConfig(String authConfig) {
        return Optional.ofNullable(authConfigs().get(authConfig));
    }
}
