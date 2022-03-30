package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.config.Config;

public final class CircuitBreakerConfigurationParser {

    private CircuitBreakerConfigurationParser() {
    }

    /**
     * Parses the {@link Config} and returns a {@link Map} of class names and their methods that should be configured with
     * circuit breaker.
     *
     * @return a {@link Map} of class names and their methods that should be configured with circuit breaker
     */
    public static Map<String, List<String>> parse(Config config) {
        List<String> filteredPropertyNames = filterPropertyNames(config.getPropertyNames()).stream()
                .filter(property -> config.getOptionalValue(property, Boolean.class).orElse(false))
                .collect(Collectors.toList());

        return filteredPropertyNames.stream()
                .map(CircuitBreakerConfigurationParser::getClassName)
                .distinct()
                .collect(Collectors.toUnmodifiableMap(
                        Function.identity(),
                        className -> getMethodNames(className, filteredPropertyNames)));
    }

    private static List<String> getMethodNames(String className, List<String> propertyNames) {
        return propertyNames.stream()
                .filter(propertyName -> propertyName.startsWith(className + "/"))
                .map(CircuitBreakerConfigurationParser::getMethodName)
                .collect(Collectors.toUnmodifiableList());
    }

    private static String getClassName(String propertyName) {
        return propertyName.substring(0, propertyName.indexOf("/"));
    }

    private static String getMethodName(String propertyName) {
        return propertyName.substring(propertyName.indexOf("/") + 1, propertyName.indexOf("/CircuitBreaker/"));
    }

    private static List<String> filterPropertyNames(Iterable<String> propertyNames) {
        return StreamSupport.stream(propertyNames.spliterator(), false)
                .filter(propertyName -> propertyName.matches(".+/.+/CircuitBreaker/enabled"))
                .collect(Collectors.toList());
    }
}
