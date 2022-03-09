package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public final class CircuitBreakerConfigurationParser {

    private final UnaryOperator<String> nameToValuePropertyMapper;

    public CircuitBreakerConfigurationParser(UnaryOperator<String> nameToValuePropertyMapper) {
        this.nameToValuePropertyMapper = nameToValuePropertyMapper;
    }

    /**
     * Parses the properties and returns a map of class names and their methods that should be configured with circuit breaker.
     *
     * @return a map of class names and their methods that should be configured with circuit breaker
     */
    public Map<String, List<String>> parse(Collection<String> propertyNames) {
        List<String> filteredPropertyNames = filterPropertyNames(propertyNames).stream()
                .filter(property -> nameToValuePropertyMapper.apply(property).equals("true"))
                .collect(Collectors.toList());

        Set<String> classNames = filteredPropertyNames.stream()
                .map(this::getClassName)
                .collect(Collectors.toSet());

        return classNames.stream().collect(Collectors.toUnmodifiableMap(
                Function.identity(),
                className -> getMethodNames(className, filteredPropertyNames)));
    }

    private List<String> getMethodNames(String className, List<String> propertyNames) {
        return propertyNames.stream()
                .filter(propertyName -> propertyName.startsWith(className + "/"))
                .map(this::getMethodName)
                .collect(Collectors.toUnmodifiableList());
    }

    private String getClassName(String propertyName) {
        return propertyName.substring(0, propertyName.indexOf("/"));
    }

    private String getMethodName(String propertyName) {
        return propertyName.substring(propertyName.indexOf("/") + 1, propertyName.indexOf("/CircuitBreaker/"));
    }

    private List<String> filterPropertyNames(Collection<String> propertyNames) {
        return propertyNames.stream()
                .filter(propertyName -> propertyName
                        .matches(".+/.+/CircuitBreaker/enabled"))
                .collect(Collectors.toList());
    }
}
