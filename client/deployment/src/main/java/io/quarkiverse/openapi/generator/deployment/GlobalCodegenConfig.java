package io.quarkiverse.openapi.generator.deployment;

import java.util.Optional;

/*
 * Model for the configuration of this extension.
 * It's used for documentation purposes only.
 * The configuration is consumed in the codegen phase, before build time.
 * Not meant to be used outside this scope.
 * Config items can be applied only globally
 */
public interface GlobalCodegenConfig {

    /**
     * Whether to log the internal generator codegen process in the default output or not.
     */
    boolean verbose();

    /**
     * Option to change the directory where OpenAPI files must be found.
     */
    Optional<String> inputBaseDir();

    /**
     * Whether or not to skip validating the input spec prior to generation. By default, invalid specifications will result in
     * an error.
     */
    boolean validateSpec();

    /**
     * Option to specify files for which generation should be executed only
     */
    Optional<String> include();

    /**
     * Option to exclude file from generation
     */
    Optional<String> exclude();

    /**
     * Create security for the referenced security scheme
     */
    Optional<String> defaultSecuritySchema();

}
