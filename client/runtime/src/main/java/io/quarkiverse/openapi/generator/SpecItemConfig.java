package io.quarkiverse.openapi.generator;

import java.util.Optional;

/**
 * This class represents the runtime authentication related configurations for the individual OpenApi spec definitions,
 * i.e. the provided files.
 */
public interface SpecItemConfig {

    /**
     * Authentication related configurations for the different securitySchemes present on a given OpenApi spec
     * definition file.
     * <p>
     * For example, given a file named petstore.json, the following prefix must be used to configure the authentication
     * related information quarkus.openapi-generator.petstore_json.auth
     *
     * @see AuthsConfig
     */
    Optional<AuthsConfig> auth();
}
