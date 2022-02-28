package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.quarkiverse.openapi.generator.deployment.circuitbreaker.CircuitBreakerConfiguration.CircuitBreakerClassConfiguration;

public final class CircuitBreakerConfigurationParser {

    private final UnaryOperator<String> nameToValuePropertyMapper;

    public CircuitBreakerConfigurationParser(UnaryOperator<String> nameToValuePropertyMapper) {
        this.nameToValuePropertyMapper = nameToValuePropertyMapper;
    }

    public CircuitBreakerConfiguration parse(Collection<String> propertyNames) {
        return new CircuitBreakerConfiguration(readClasses(propertyNames));
    }

    private List<CircuitBreakerClassConfiguration> readClasses(Collection<String> propertyNames) {
        List<String> filteredPropertyNames = filterPropertyNames(propertyNames).stream()
                .filter(property -> nameToValuePropertyMapper.apply(property).equals("true"))
                .collect(Collectors.toList());

        Set<String> classNames = filteredPropertyNames.stream()
                .map(this::getClassName)
                .collect(Collectors.toSet());

        return classNames.stream()
                .map(className -> new CircuitBreakerClassConfiguration(className,
                        getMethodNames(className, filteredPropertyNames)))
                .collect(Collectors.toUnmodifiableList());
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
