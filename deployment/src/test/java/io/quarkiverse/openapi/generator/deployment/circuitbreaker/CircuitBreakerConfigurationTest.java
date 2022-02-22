package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CircuitBreakerConfigurationTest {

    @Test
    void getAttributesAsString() {
        Map<String, String> attributes = new HashMap<>();

        attributes.put("failOn", "java.lang.IllegalArgumentException,java.lang.NullPointerException");
        attributes.put("skipOn", "java.lang.NumberFormatException");
        attributes.put("delay", "33");
        attributes.put("delayUnit", "MILLIS");
        attributes.put("requestVolumeThreshold", "42");
        attributes.put("failureRatio", "3.14");
        attributes.put("successThreshold", "22");

        CircuitBreakerConfiguration.Operation operation = new CircuitBreakerConfiguration.Operation("any", attributes);

        assertThat(operation.getAttributesAsString())
                .isEqualTo("delay = 33, " +
                        "delayUnit = java.time.temporal.ChronoUnit.MILLIS, " +
                        "failOn = { java.lang.IllegalArgumentException.class, java.lang.NullPointerException.class }, " +
                        "failureRatio = 3.14, " +
                        "requestVolumeThreshold = 42, " +
                        "skipOn = java.lang.NumberFormatException.class, " +
                        "successThreshold = 22");
    }
}
