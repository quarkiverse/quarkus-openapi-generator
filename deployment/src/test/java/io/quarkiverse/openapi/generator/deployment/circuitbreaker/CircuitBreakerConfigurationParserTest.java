package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.Test;

import io.smallrye.config.PropertiesConfigSource;

import static org.assertj.core.api.Assertions.assertThat;

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
        PropertiesConfigSource configSource;
        try {
            configSource = new PropertiesConfigSource(getResource(propertiesFile));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return ConfigProviderResolver
                .instance()
                .getBuilder()
                .withSources(configSource)
                .build();
    }

    private static URL getResource(String resourcePath) {
        return Objects.requireNonNull(CircuitBreakerConfigurationParserTest.class.getResource(resourcePath));
    }
}
