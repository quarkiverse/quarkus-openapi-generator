package io.quarkiverse.openapi.generator.deployment;

import static io.quarkiverse.openapi.generator.deployment.OpenApiGeneratorBuildTimeConfig.CONFIG_BUILD_NAME;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME, name = CONFIG_BUILD_NAME)
public class OpenApiGeneratorBuildTimeConfig {
    public static final String CONFIG_BUILD_NAME = "openapi-generator.codegen";

    static final String BUILD_TIME_CONFIG_PREFIX = "quarkus." + CONFIG_BUILD_NAME;

    /**
     * Fine tune the configuration for each OpenAPI spec file in `src/openapi` directory.
     * <p>
     * The file name is used to index this configuration. For example:
     * `quarkus.openapi-generator.codegen.spec."myfilespec.json".base-package=org.acme`.
     **/
    @ConfigItem(name = "spec")
    public Map<String, SpecConfig> specs;

    /**
     * Increases the internal generator log output verbosity
     */
    @ConfigItem(defaultValue = "false")
    public String verbose;
}
