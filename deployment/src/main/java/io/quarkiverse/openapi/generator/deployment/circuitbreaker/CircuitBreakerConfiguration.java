package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import java.util.List;

public final class CircuitBreakerConfiguration {

    private final List<CircuitBreakerClassConfiguration> classConfigurations;

    public CircuitBreakerConfiguration(List<CircuitBreakerClassConfiguration> classConfigurations) {
        this.classConfigurations = List.copyOf(classConfigurations);
    }

    public List<CircuitBreakerClassConfiguration> getClassConfigurations() {
        return classConfigurations;
    }

    CircuitBreakerClassConfiguration getClassConfiguration(String className) {
        return getClassConfigurations().stream()
                .filter(c -> c.getClassName().equals(className))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Configuration not found for class: " + className));
    }

    public static final class CircuitBreakerClassConfiguration {

        private final String className;

        private final List<String> methods;

        public CircuitBreakerClassConfiguration(String className, List<String> methods) {
            this.className = className;
            this.methods = List.copyOf(methods);
        }

        public String getClassName() {
            return className;
        }

        public List<String> getMethods() {
            return methods;
        }
    }
}
