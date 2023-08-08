package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.SUPPORTED_CONFIGURATIONS;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.quarkus.bootstrap.prebuild.CodeGenException;

public class OpenApiConfigValidator {

    static final Pattern CONFIG_PATTERN = Pattern.compile(
            "quarkus\\.openapi-generator\\.codegen\\.(spec.(?<specId>[A-Za-z0-9_]*)\\.)?(?<configName>[A-Za-z0-9_\\-]*)\\.?(?<configMap>.*)?");

    public static void validateInputConfiguration(List<String> configNames) throws CodeGenException {
        List<String> userOpenApiConfigurations = configNames.stream()
                .filter(pn -> pn.startsWith("quarkus.openapi-generator.codegen"))
                .map(CONFIG_PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group("configName"))
                .distinct()
                .collect(Collectors.toList());

        if (!userOpenApiConfigurations.isEmpty()) {

            List<String> unsupportedConfigNames = userOpenApiConfigurations.stream()
                    .filter(uc -> !SUPPORTED_CONFIGURATIONS.contains(uc)).collect(Collectors.toList());

            if (!unsupportedConfigNames.isEmpty()) {
                throw new CodeGenException(
                        "Found unsupported configuration: " + unsupportedConfigNames + ". Supported configurations are :"
                                + SUPPORTED_CONFIGURATIONS);
            }
        }
    }
}
