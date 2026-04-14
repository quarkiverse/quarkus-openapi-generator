package io.quarkiverse.openapi.generator.it.name.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.acme.openapi.animals.api.PrimateApi;
import org.acme.openapi.animals.model.Primate;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(WiremockTestResource.class)
class QuarkusNameMappingTest {

    @RestClient
    @Inject
    PrimateApi api;

    @Test
    void primateHasAllFields() {
        Primate primate = api.getPrimateById(1L);

        assertThat(primate).isNotNull();
        assertThat(primate.getId()).isEqualTo(1L);
        assertThat(primate.getName()).isEqualTo("Mary Jane");
        assertThat(primate.getBornDate()).isEqualTo("1970-01-01T01:01:01.001Z");
        assertThat(primate.getDeathDate()).isEqualTo("2020-01-01T01:01:01.001Z");
    }
}
