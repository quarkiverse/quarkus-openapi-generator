package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model;

public class Path {

    private final String value;

    private Path(String value) {
        this.value = value;
    }

    public static Path create(final String value) {
        return new Path(value);
    }

    public String getValue() {
        return this.value;
    }

}