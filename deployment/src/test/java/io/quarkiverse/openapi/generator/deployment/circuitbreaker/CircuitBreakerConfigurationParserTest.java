package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class CircuitBreakerConfigurationParserTest {

    @Test
    void parse() throws IOException {
        Map<String, List<String>> circuitBreakerConfiguration = loadConfiguration("/circuitbreaker/application.properties");

        assertThat(circuitBreakerConfiguration.get("org.acme.CountryResource"))
                .containsExactlyInAnyOrder("getCountries", "getByCapital");

        assertThat(circuitBreakerConfiguration.get("org.acme.CityResource"))
                .containsOnly("get");
    }

    private Map<String, List<String>> loadConfiguration(String propertiesFile) throws IOException {
        Properties properties = new Properties();
        properties.load(CircuitBreakerConfigurationParserTest.class.getResourceAsStream(propertiesFile));

        return new CircuitBreakerConfigurationParser(properties::getProperty)
                .parse(properties.stringPropertyNames());
    }

    @Test
    void circuitBreakerDisabledShouldReturnEmptyConfig() throws IOException {
        Map<String, List<String>> circuitBreakerConfiguration = loadConfiguration(
                "/circuitbreaker/circuit_breaker_disabled_application.properties");

        assertThat(circuitBreakerConfiguration).isEmpty();
    }

    @Test
    void missingCircuitBreakerEnabledConfigShouldReturnEmptyConfig() throws IOException {
        Map<String, List<String>> circuitBreakerConfiguration = loadConfiguration(
                "/circuitbreaker/missing_circuit_breaker_enabled_application.properties");

        assertThat(circuitBreakerConfiguration).isEmpty();
    }
}
