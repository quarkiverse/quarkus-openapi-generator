package io.quarkiverse.openapi.generator.deployment;

import java.util.List;
import java.util.Optional;

import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/*
 * Model for the configuration of this extension.
 * It's used for documentation purposes only.
 * The configuration is consumed in the codegen phase, before build time.
 * Not meant to be used outside this scope.
 * Config items can be applied only on gav
 */
public interface GavItemConfig extends CommonItemConfig {
    /**
     * List of OpenAPI spec files in GAV to be generated
     */
    @WithName("spec-files")
    @WithDefault("openapi.yaml")
    Optional<List<String>> gavSpecFiles();
}
