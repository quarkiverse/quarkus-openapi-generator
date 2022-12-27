package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.array_enum_yaml.api.ArrayEnumResourceApi;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ArrayEnumTest {

    @RestClient
    @Inject
    ArrayEnumResourceApi api;

    @Test
    void apiIsBeingGenerated() {
        assertThat(api).isNotNull();
    }
}
