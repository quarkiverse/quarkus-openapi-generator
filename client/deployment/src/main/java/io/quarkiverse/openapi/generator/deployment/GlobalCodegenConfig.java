package io.quarkiverse.openapi.generator.deployment;

import java.util.Optional;

import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/*
 * Model for the configuration of this extension.
 * It's used for documentation purposes only.
 * The configuration is consumed in the codegen phase, before build time.
 * Not meant to be used outside this scope.
 * Config items can be applied only globally
 */
public interface GlobalCodegenConfig extends CommonItemConfig {

    /**
     * Whether to log the internal generator codegen process in the default output or not.
     */
    @WithDefault("false")
    @WithName("verbose")
    boolean verbose();

    /**
     * Option to change the directory where OpenAPI files must be found.
     */
    @WithName("input-base-dir")
    Optional<String> inputBaseDir();

    /**
     * Option to change the directory where template files must be found.
     */
    @WithName("template-base-dir")
    Optional<String> templateBaseDir();

    /**
     * Whether or not to skip validating the input spec prior to generation. By default, invalid specifications will result in
     * an error.
     */
    @WithName("validateSpec")
    @WithDefault("true")
    boolean validateSpec();

    /**
     * Option to specify files for which generation should be executed only
     */
    @WithName("include")
    Optional<String> include();

    /**
     * Option to exclude file from generation
     */
    @WithName("exclude")
    Optional<String> exclude();

    /**
     * Create security for the referenced security scheme
     */
    @WithName("default-security-scheme")
    Optional<String> defaultSecuritySchema();

}
