package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.Test;

class CircuitBreakerConfigurationParserTest {

    @Test
    void parse() {
        Config config = mockConfig("/circuitbreaker/application.properties");

        Map<String, List<String>> circuitBreakerConfiguration = CircuitBreakerConfigurationParser.parse(config);

        assertThat(circuitBreakerConfiguration.get("org.acme.CountryResource"))
                .containsExactlyInAnyOrder("getCountries", "getByCapital");

        assertThat(circuitBreakerConfiguration.get("org.acme.CityResource"))
                .containsOnly("get");
    }

    @Test
    void circuitBreakerDisabledShouldReturnEmptyConfig() {
        Config config = mockConfig("/circuitbreaker/circuit_breaker_disabled_application.properties");

        Map<String, List<String>> circuitBreakerConfiguration = CircuitBreakerConfigurationParser.parse(config);

        assertThat(circuitBreakerConfiguration).isEmpty();
    }

    @Test
    void missingCircuitBreakerEnabledConfigShouldReturnEmptyConfig() {
        Config config = mockConfig("/circuitbreaker/missing_circuit_breaker_enabled_application.properties");

        Map<String, List<String>> circuitBreakerConfiguration = CircuitBreakerConfigurationParser.parse(config);

        assertThat(circuitBreakerConfiguration).isEmpty();
    }

    private static Config mockConfig(String propertiesFile) {
        return ConfigProviderResolver
                .instance()
                .getBuilder()
                .withSources(new FileConfigSource(propertiesFile))
                .build();
    }
}
