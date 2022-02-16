package io.quarkiverse.openapi.generator.deployment;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME, name = "openapi-generator")
public class OpenApiGeneratorConfiguration {
    static final String CONFIG_PREFIX = "quarkus.openapi-generator";

    /**
     * Fine tune the configuration for each OpenAPI spec file in `src/openapi` directory.
     * <p>
     * The file name is used to index this configuration. For example:
     * `quarkus.openapi-generator.spec."myfilespec.json".base-package=org.acme`.
     **/
    @ConfigItem(name = "spec")
    public Map<String, SpecConfig> specs;

    /**
     * Increases the internal generator log output verbosity
     */
    @ConfigItem(defaultValue = "false")
    public String verbose;
}
