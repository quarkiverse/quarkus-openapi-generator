package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.SpecConfig.resolveModelPackage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.config.Config;

/**
 * Extracts the Model codegen properties from a given {@link Config} reference.
 * These properties are then injected in the OpenAPI generator to tweak the code generation properties.
 */
public final class ModelCodegenConfigParser {

    public static Map<String, Object> parse(final Config config, final String basePackage) {
        final List<String> modelProperties = filterModelPropertyNames(config.getPropertyNames(),
                resolveModelPackage(basePackage));
        final Map<String, Object> modelConfig = new HashMap<>();
        modelProperties.forEach(m -> modelConfig.put(m, config.getValue(m, String.class)));
        return modelConfig;
    }

    private static List<String> filterModelPropertyNames(final Iterable<String> propertyNames, final String modelPackage) {
        return StreamSupport.stream(propertyNames.spliterator(), false)
                .filter(propertyName -> propertyName.startsWith(modelPackage))
                .collect(Collectors.toList());
    }
}
