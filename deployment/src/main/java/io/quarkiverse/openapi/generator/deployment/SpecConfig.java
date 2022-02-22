package io.quarkiverse.openapi.generator.deployment;

import static io.quarkiverse.openapi.generator.deployment.OpenApiGeneratorConfiguration.CONFIG_PREFIX;

import java.nio.file.Path;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class SpecConfig {
    public static final String API_PKG_SUFFIX = ".api";
    public static final String MODEL_PKG_SUFFIX = ".model";
    static final String BASE_PACKAGE_PROP_FORMAT = CONFIG_PREFIX + ".spec.\"%s\".base-package";

    /**
     * Defines the base package name for the generated classes.
     */
    @ConfigItem
    public String basePackage;

    public String getApiPackage() {
        return String.format("%s%s", basePackage, API_PKG_SUFFIX);
    }

    public String getModelPackage() {
        return String.format("%s%s", basePackage, MODEL_PKG_SUFFIX);
    }

    public static String getResolvedBasePackageProperty(final Path openApiFilePath) {
        final String uriFilePath = openApiFilePath.toUri().toString();
        final String fileName = uriFilePath.substring(uriFilePath.lastIndexOf("/") + 1);
        return String.format(BASE_PACKAGE_PROP_FORMAT, fileName);
    }
}
