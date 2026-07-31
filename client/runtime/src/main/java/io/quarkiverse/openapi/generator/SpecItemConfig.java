package io.quarkiverse.openapi.generator;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * This class represents the runtime authentication related configurations for the individual OpenApi spec definitions,
 * i.e. the provided files.
 */
@ConfigGroup
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
    AuthsConfig auth();

    /**
     * When set to true, only the first successful authentication provider is applied per request (OR semantics).
     * When false (default), all matching authentication providers are applied (AND semantics).
     */
    @WithName("exclusive-auth")
    @WithDefault("false")
    boolean exclusiveAuth();

    default Optional<AuthsConfig> getAuth() {
        return Optional.ofNullable(auth());
    }
}
