package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public final class CircuitBreakerConfigurationParser {

    private static final String CONFIG_PREFIX = "quarkus.openapi-generator.spec.";

    private static final String PROPERTY_REGEX = CONFIG_PREFIX + "\".+\"/.+/CircuitBreaker/.+";

    private static final String CIRCUIT_BREAKER_ENABLED_PROPERTY_NAME = "quarkus.openapi-generator.CircuitBreaker.enabled";

    private final UnaryOperator<String> nameToValuePropertyMapper;

    private final String openApiFileName;

    private final int operationIndex;

    public CircuitBreakerConfigurationParser(String openApiFileName, UnaryOperator<String> nameToValuePropertyMapper) {
        this.openApiFileName = openApiFileName;
        this.nameToValuePropertyMapper = nameToValuePropertyMapper;
        operationIndex = CONFIG_PREFIX.length() + openApiFileName.length() + 3;
    }

    public CircuitBreakerConfiguration parse(Collection<String> propertyNames) {
        if (propertyNames.contains(CIRCUIT_BREAKER_ENABLED_PROPERTY_NAME)
                && Boolean.parseBoolean(nameToValuePropertyMapper.apply(CIRCUIT_BREAKER_ENABLED_PROPERTY_NAME))) {
            return CircuitBreakerConfiguration.builder()
                    .enabled(true)
                    .operations(getOperations(propertyNames))
                    .build();
        } else {
            return CircuitBreakerConfiguration.empty();
        }
    }

    private List<CircuitBreakerConfiguration.Operation> getOperations(Collection<String> propertyNames) {
        Map<String, Map<String, String>> operationsMap = new HashMap<>();

        for (String propertyName : filterPropertyNames(propertyNames)) {
            String operationName = getOperationName(propertyName);
            String circuitBreakerAttributeName = getCircuitBreakerAttributeName(propertyName);
            String circuitBreakerAttributeValue = nameToValuePropertyMapper.apply(propertyName).trim();

            operationsMap.computeIfAbsent(operationName, k -> new HashMap<>())
                    .put(circuitBreakerAttributeName, circuitBreakerAttributeValue);
        }

        return operationsMap.entrySet().stream()
                .map(entry -> new CircuitBreakerConfiguration.Operation(entry.getKey(), entry.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    private String getCircuitBreakerAttributeName(String propertyName) {
        return propertyName.substring(propertyName.lastIndexOf("/") + 1);
    }

    private String getOperationName(String propertyName) {
        return propertyName.substring(operationIndex, propertyName.indexOf("/CircuitBreaker/"));
    }

    private List<String> filterPropertyNames(Collection<String> propertyNames) {
        return propertyNames.stream()
                .filter(propertyName -> propertyName.matches(PROPERTY_REGEX))
                .filter(propertyName -> propertyName
                        .matches(CONFIG_PREFIX + "\"" + openApiFileName + "\"/.+/CircuitBreaker/.+"))
                .collect(Collectors.toList());
    }
}
