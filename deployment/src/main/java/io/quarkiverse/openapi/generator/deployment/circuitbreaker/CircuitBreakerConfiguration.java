package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CircuitBreakerConfiguration {

    private static final CircuitBreakerConfiguration EMPTY = CircuitBreakerConfiguration.builder()
            .enabled(false)
            .operations(List.of())
            .build();

    private final Boolean enabled;

    private final List<Operation> operations;

    private CircuitBreakerConfiguration(Builder builder) {
        enabled = Objects.requireNonNull(builder.enabled);
        operations = Objects.requireNonNull(builder.operations);
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public static CircuitBreakerConfiguration empty() {
        return EMPTY;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Operation {
        private final String name;
        private final Map<String, String> attributes;

        public Operation(String name, Map<String, String> attributes) {
            this.name = Objects.requireNonNull(name);
            this.attributes = Objects.requireNonNull(attributes);
        }

        public String getName() {
            return name;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public String getAttributesAsString() {
            return attributes.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> {
                        switch (entry.getKey()) {
                            case "failOn":
                            case "skipOn":
                                List<String> classes = Stream.of(entry.getValue().split(","))
                                        .map(String::trim)
                                        .map(value -> value + ".class")
                                        .collect(Collectors.toUnmodifiableList());

                                if (classes.size() == 1) {
                                    return entry.getKey() + " = " + classes.get(0);
                                } else {
                                    return entry.getKey() + " = { " + String.join(", ", classes) + " }";
                                }
                            case "delayUnit":
                                return "delayUnit = java.time.temporal.ChronoUnit." + entry.getValue();
                            default:
                                return entry.getKey() + " = " + entry.getValue().trim();
                        }
                    }).collect(Collectors.joining(", "));
        }
    }

    public static final class Builder {
        private List<Operation> operations;
        private Boolean enabled;

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder operations(List<Operation> operations) {
            this.operations = List.copyOf(operations);
            return this;
        }

        public CircuitBreakerConfiguration build() {
            return new CircuitBreakerConfiguration(this);
        }
    }
}
