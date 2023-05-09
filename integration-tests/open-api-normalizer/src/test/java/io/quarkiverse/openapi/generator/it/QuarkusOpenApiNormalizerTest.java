package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.acme.openapi.animals.api.PrimateApi;
import org.acme.openapi.animals.model.Primate;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(WiremockTestResource.class)
class QuarkusOpenApiNormalizerTest {

    @RestClient
    @Inject
    PrimateApi api;

    @Test
    void primateHasAllFields() {
        Primate primate = api.getPrimateById(1L);

        assertThat(primate).isNotNull();
        assertThat(primate.getId()).isEqualTo(1L);
        assertThat(primate.getName()).isEqualTo("Jane Doe");
        assertThat(primate.getBorn()).isNotNull();
        assertThat(primate.getDeceased()).isNotNull();
    }
}
