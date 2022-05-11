package io.quarkiverse.openapi.generator;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/**
 * This class represents the runtime authentication related configurations for the individual OpenApi spec definitions,
 * i.e. the provided files.
 */
@ConfigGroup
public class SpecItemConfig {

    /**
     * Authentication related configurations for the different securitySchemes present on a given OpenApi spec
     * definition file.
     * <p>
     * For example, given a file named petstore.json, the following prefix must be used to configure the authentication
     * related information quarkus.openapi-generator.petstore_json.auth
     * 
     * @see SpecItemAuthsConfig
     */
    @ConfigItem
    public SpecItemAuthsConfig auth;

    public Optional<SpecItemAuthsConfig> getAuth() {
        return Optional.ofNullable(auth);
    }

    @Override
    public String toString() {
        return "SpecItemConfig{" +
                "auth=" + auth +
                '}';
    }
}