package io.quarkiverse.openapi.generator.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/*
 * Model for the configuration of this extension.
 * It's used for documentation purposes only.
 * The configuration is consumed in the codegen phase, before build time.
 * Not meant to be used outside this scope.
 * Config items can be applied only globally
 */
@ConfigGroup
public class GlobalCodegenConfig extends CommonItemConfig {

    /**
     * Whether to log the internal generator codegen process in the default output or not.
     */
    @ConfigItem(name = "verbose", defaultValue = "false")
    public boolean verbose;

    /**
     * Option to change the directory where OpenAPI files must be found.
     */
    @ConfigItem(name = "input-base-dir")
    public Optional<String> inputBaseDir;

    /**
     * Whether or not to skip validating the input spec prior to generation. By default, invalid specifications will result in
     * an error.
     */
    @ConfigItem(name = "validateSpec", defaultValue = "true")
    public boolean validateSpec;

    /**
     * Option to specify files for which generation should be executed only
     */
    @ConfigItem(name = "include")
    public Optional<String> include;

    /**
     * Option to exclude file from generation
     */
    @ConfigItem(name = "exclude")
    public Optional<String> exclude;

    /**
     * Create security for the referenced security scheme
     */
    @ConfigItem(name = "default-security-scheme")
    public Optional<String> defaultSecuritySchema;

}
