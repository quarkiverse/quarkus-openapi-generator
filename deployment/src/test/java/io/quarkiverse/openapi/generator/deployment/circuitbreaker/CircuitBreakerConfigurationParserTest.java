package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class CircuitBreakerConfigurationParserTest {

    @Test
    void parse() throws IOException {
        CircuitBreakerConfiguration circuitBreakerConfiguration = loadConfiguration("/circuitbreaker/application.properties");

        assertThat(circuitBreakerConfiguration.getClassConfiguration("org.acme.CountryResource").getMethods())
                .containsExactlyInAnyOrder("getCountries", "getByCapital");

        assertThat(circuitBreakerConfiguration.getClassConfiguration("org.acme.CityResource").getMethods())
                .containsOnly("get");
    }

    private CircuitBreakerConfiguration loadConfiguration(String propertiesFile) throws IOException {
        Properties properties = new Properties();
        properties.load(CircuitBreakerConfigurationParserTest.class.getResourceAsStream(propertiesFile));

        return new CircuitBreakerConfigurationParser(properties::getProperty)
                .parse(properties.stringPropertyNames());
    }

    @Test
    void circuitBreakerDisabledShouldReturnEmptyConfig() throws IOException {
        CircuitBreakerConfiguration circuitBreakerConfiguration = loadConfiguration(
                "/circuitbreaker/circuit_breaker_disabled_application.properties");

        assertThat(circuitBreakerConfiguration.getClassConfigurations()).isEmpty();
    }

    @Test
    void missingCircuitBreakerEnabledConfigShouldReturnEmptyConfig() throws IOException {
        CircuitBreakerConfiguration circuitBreakerConfiguration = loadConfiguration(
                "/circuitbreaker/missing_circuit_breaker_enabled_application.properties");

        assertThat(circuitBreakerConfiguration.getClassConfigurations()).isEmpty();
    }
}
