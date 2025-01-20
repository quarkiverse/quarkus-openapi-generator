package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.SUPPORTED_CONFIGURATIONS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.bootstrap.prebuild.CodeGenException;

public final class OpenApiConfigValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiConfigValidator.class);

    static final Pattern CONFIG_PATTERN = Pattern.compile(
            "quarkus\\.openapi-generator\\.codegen\\.(spec.(?<specId>[\\w\\-]*)\\.)?(?<configName>[A-Za-z0-9_\\-]*)\\.?(?<configMap>.+)?");

    private OpenApiConfigValidator() {
    }

    public static void validateInputConfiguration(List<String> configNames) throws CodeGenException {
        List<Matcher> userOpenApiConfigurations = configNames.stream()
                .filter(pn -> pn.startsWith("quarkus.openapi-generator.codegen"))
                .map(CONFIG_PATTERN::matcher)
                .filter(Matcher::find)
                .collect(Collectors.toList());

        if (!userOpenApiConfigurations.isEmpty()) {
            Set<String> unsupportedConfigNames = new HashSet<>();
            for (Matcher userOpenApiConfiguration : userOpenApiConfigurations) {
                String configName = userOpenApiConfiguration.group("configName");
                if (configName != null && !SUPPORTED_CONFIGURATIONS.contains(configName)) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("Unsupported configuration : " + userOpenApiConfiguration.group());
                    }
                    unsupportedConfigNames.add(configName);
                }
            }
            if (!unsupportedConfigNames.isEmpty()) {
                throw new CodeGenException(
                        "Found unsupported configuration: " + unsupportedConfigNames + ". Supported configurations are :"
                                + SUPPORTED_CONFIGURATIONS);
            }
        }
    }
}
