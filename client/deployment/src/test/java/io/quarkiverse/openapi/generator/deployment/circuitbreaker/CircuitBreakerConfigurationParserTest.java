package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.deployment.MockConfigUtils;

class CircuitBreakerConfigurationParserTest {

    @Test
    void parse() {
        Config config = MockConfigUtils.getTestConfig("/circuitbreaker/application.properties");

        Map<String, List<String>> circuitBreakerConfiguration = CircuitBreakerConfigurationParser.parse(config);

        assertThat(circuitBreakerConfiguration.get("org.acme.CountryResource"))
                .containsExactlyInAnyOrder("getCountries", "getByCapital");

        assertThat(circuitBreakerConfiguration.get("org.acme.CityResource"))
                .containsOnly("get");
    }

    @Test
    void circuitBreakerDisabledShouldReturnEmptyConfig() {
        Config config = MockConfigUtils.getTestConfig("/circuitbreaker/circuit_breaker_disabled_application.properties");

        Map<String, List<String>> circuitBreakerConfiguration = CircuitBreakerConfigurationParser.parse(config);

        assertThat(circuitBreakerConfiguration).isEmpty();
    }

    @Test
    void missingCircuitBreakerEnabledConfigShouldReturnEmptyConfig() {
        Config config = MockConfigUtils.getTestConfig("/circuitbreaker/missing_circuit_breaker_enabled_application.properties");

        Map<String, List<String>> circuitBreakerConfiguration = CircuitBreakerConfigurationParser.parse(config);

        assertThat(circuitBreakerConfiguration).isEmpty();
    }
}
