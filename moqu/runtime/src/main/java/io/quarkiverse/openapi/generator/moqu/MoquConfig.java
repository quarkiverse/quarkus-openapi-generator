package io.quarkiverse.openapi.generator.moqu;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.openapi-generator.moqu")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface MoquConfig {

    String DEFAULT_RESOURCE_DIR = "openapi";

    /**
     * Path to the Moqu OpenAPI files, relative to the <code>src/main/resources</code> directory.
     */
    @WithDefault(DEFAULT_RESOURCE_DIR)
    String resourceDir();
}
