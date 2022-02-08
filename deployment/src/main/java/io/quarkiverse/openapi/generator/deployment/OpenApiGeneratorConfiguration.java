package io.quarkiverse.openapi.generator.deployment;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME, name = "openapi-generator")
public class OpenApiGeneratorConfiguration {
    /**
     * Fine tune the configuration for each OpenAPI spec file in `src/openapi` directory.
     * <p>
     * The file name is used to index this configuration. For example:
     * `quarkus.openapi-generator.spec."myfilespec.json".api-package=org.acme.api`.
     * <p>
     * If you have more than one file to generate rest clients, it is **highly** recommended that you add this configuration
     * to your properties file to make a distinction between the generated APIs.
     **/
    @ConfigItem(name = "spec")
    public Map<String, SpecConfig> specs;

    /**
     * Increases the internal generator log output verbosity
     */
    @ConfigItem(defaultValue = "false")
    public String verbose;
}
