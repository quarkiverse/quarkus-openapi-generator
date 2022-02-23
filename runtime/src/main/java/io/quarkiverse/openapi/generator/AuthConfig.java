package io.quarkiverse.openapi.generator;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class AuthConfig {

    /**
     * The authentication username
     */
    @ConfigItem
    public String username;

    /**
     * The authentication password
     */
    @ConfigItem
    public String password;

}
