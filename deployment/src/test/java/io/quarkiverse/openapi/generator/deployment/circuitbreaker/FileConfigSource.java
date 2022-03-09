package io.quarkiverse.openapi.generator.deployment.circuitbreaker;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.spi.ConfigSource;

final class FileConfigSource implements ConfigSource {

    private final String fileName;

    private final Map<String, String> properties;

    FileConfigSource(String fileName) {
        this.fileName = fileName;

        Properties properties = new Properties();
        try {
            properties.load(FileConfigSource.class.getResourceAsStream(fileName));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        this.properties = properties.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public String getValue(String s) {
        return properties.get(s);
    }

    @Override
    public String getName() {
        return fileName;
    }
}
