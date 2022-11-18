package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.resolveApiPackage;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.resolveModelPackage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.config.Config;

/**
 * Extracts the codegen properties from a given {@link Config} reference.
 * These properties are then injected in the OpenAPI generator to tweak the code generation properties.
 */
public final class ClassCodegenConfigParser {

    public static Map<String, Object> parse(final Config config, final String basePackage) {
        final List<String> modelProperties = filterPropertyNames(config.getPropertyNames(),
                resolveModelPackage(basePackage));
        final List<String> apiProperties = filterPropertyNames(config.getPropertyNames(),
                resolveApiPackage(basePackage));
        modelProperties.addAll(apiProperties);
        final Map<String, Object> modelConfig = new HashMap<>();
        modelProperties.forEach(m -> modelConfig.put(m, config.getValue(m, String.class)));
        return modelConfig;
    }

    private static List<String> filterPropertyNames(final Iterable<String> propertyNames, final String findPackage) {
        return StreamSupport.stream(propertyNames.spliterator(), false)
                .filter(propertyName -> propertyName.startsWith(findPackage))
                .collect(Collectors.toList());
    }
}
