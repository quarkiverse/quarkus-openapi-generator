package io.quarkiverse.openapi.server.generator.deployment;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Objects;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import io.smallrye.config.PropertiesConfigSource;

public final class MockConfigUtils {

    private MockConfigUtils() {
    }

    public static Config getTestConfig(String propertiesFile) {
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
        return Objects.requireNonNull(MockConfigUtils.class.getResource(resourcePath));
    }
}
