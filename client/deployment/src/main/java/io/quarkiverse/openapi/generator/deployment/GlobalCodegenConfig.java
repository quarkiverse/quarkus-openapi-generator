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
     * Whether or not to skip gav scanning.
     */
    @WithName("gav-scanning")
    @WithDefault("true")
    boolean gavScanning();

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
     * Option to filter artifactId from generation
     */
    @WithName("artifact-id-filter")
    @WithDefault(".*openapi.*")
    Optional<String> artifactIdFilter();

    /**
     * Option to exclude GAVs from generation
     */
    @WithName("exclude-gavs")
    Optional<List<String>> excludeGavs();

    /**
     * Option to specify GAVs for which generation should be executed only.
     *
     * Depending on the GAV Provider default behavior differs:
     * <ul>
     * <li>for {@link io.quarkiverse.openapi.generator.deployment.codegen.YamlOrJsonGAVCoordinateOpenApiSpecInputProvider}, all
     * suitable GAVs will be considered for generation if config value is not given</li>
     * <li>for {@link io.quarkiverse.openapi.generator.deployment.codegen.JarOrZipGAVCoordinateOpenApiSpecInputProvider}, only
     * specified GAVs will be considered for generation if config value is available</li>
     * </ul>
     */
    @WithName("include-gavs")
    Optional<List<String>> includeGavs();

    /**
     * Create security for the referenced security scheme
     */
    @WithName("default-security-scheme")
    Optional<String> defaultSecuritySchema();

}
