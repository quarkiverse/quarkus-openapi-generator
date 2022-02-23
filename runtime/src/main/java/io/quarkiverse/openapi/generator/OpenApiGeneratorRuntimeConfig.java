package io.quarkiverse.openapi.generator;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.RUN_TIME, name = OpenApiGeneratorRuntimeConfig.CONFIG_RUN_TIME_NAME)
public class OpenApiGeneratorRuntimeConfig {
    public static final String CONFIG_RUN_TIME_NAME = "openapi-generator";
    public static final String RUN_TIME_CONFIG_PREFIX = "quarkus." + CONFIG_RUN_TIME_NAME;

    /**
     * Fine tune the configuration for each OpenAPI spec file in `src/openapi` directory.
     * <p>
     * The file name is used to index this configuration. For example:
     * `quarkus.openapi-generator.spec."myfilespec.json".auth.username=john`.
     **/
    @ConfigItem(name = "spec")
    public Map<String, SpecConfig> specs;

}
