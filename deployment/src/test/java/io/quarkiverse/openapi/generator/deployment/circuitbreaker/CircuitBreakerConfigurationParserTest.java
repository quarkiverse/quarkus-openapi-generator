package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class CircuitBreakerConfigurationParserTest {

    @Test
    void parse() throws IOException {
        CircuitBreakerConfiguration circuitBreakerConfiguration = loadConfiguration("/circuitbreaker/application.properties");

        assertThat(circuitBreakerConfiguration.getEnabled()).isTrue();
        assertThat(circuitBreakerConfiguration.getOperations()).hasSize(2);

        circuitBreakerConfiguration.getOperations().forEach(operation -> {
            switch (operation.getName()) {
                case "getCountries":
                    Map<String, String> getCountries = operation.getAttributes();
                    assertThat(getCountries).hasSize(7);
                    assertThat(getCountries)
                            .contains(entry("failOn", "java.lang.IllegalArgumentException,java.lang.NullPointerException"));
                    assertThat(getCountries)
                            .contains(entry("skipOn", "java.lang.NumberFormatException, java.lang.IndexOutOfBoundsException"));
                    assertThat(getCountries).contains(entry("delay", "33"));
                    assertThat(getCountries).contains(entry("delayUnit", "MILLIS"));
                    assertThat(getCountries).contains(entry("requestVolumeThreshold", "42"));
                    assertThat(getCountries).contains(entry("failureRatio", "3.14"));
                    assertThat(getCountries).contains(entry("successThreshold", "22"));
                    break;

                case "getByCapital":
                    Map<String, String> getByCapital = operation.getAttributes();
                    assertThat(getByCapital).hasSize(3);
                    assertThat(getByCapital).contains(entry("skipOn", "java.lang.IndexOutOfBoundsException"));
                    assertThat(getByCapital).contains(entry("delay", "3"));
                    assertThat(getByCapital).contains(entry("successThreshold", "10"));
                    break;

                default:
                    fail("Unexpected operation: " + operation.getName());
            }
        });
    }

    private CircuitBreakerConfiguration loadConfiguration(String propertiesFile) throws IOException {
        Properties properties = new Properties();
        properties.load(
                CircuitBreakerConfigurationParserTest.class.getResourceAsStream(propertiesFile));

        String openApiFileName = "restcountries.json";

        return new CircuitBreakerConfigurationParser(openApiFileName,
                properties::getProperty)
                        .parse(properties.stringPropertyNames());
    }

    @Test
    void circuitBreakerDisabledShouldReturnEmptyConfig() throws IOException {
        CircuitBreakerConfiguration circuitBreakerConfiguration = loadConfiguration(
                "/circuitbreaker/circuit_breaker_disabled_application.properties");

        assertThat(circuitBreakerConfiguration.getEnabled()).isFalse();
        assertThat(circuitBreakerConfiguration.getOperations()).isEmpty();
    }

    @Test
    void missingCircuitBreakerEnabledConfigShouldReturnEmptyConfig() throws IOException {
        CircuitBreakerConfiguration circuitBreakerConfiguration = loadConfiguration(
                "/circuitbreaker/missing_circuit_breaker_enabled_application.properties");

        assertThat(circuitBreakerConfiguration.getEnabled()).isFalse();
        assertThat(circuitBreakerConfiguration.getOperations()).isEmpty();
    }
}
