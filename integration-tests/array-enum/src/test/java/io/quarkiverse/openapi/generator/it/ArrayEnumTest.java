package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.array_enum_yaml.api.ArrayEnumResourceApi;
import org.openapi.quarkus.array_enum_yaml.model.WebhookCreateUpdatePayload;

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

    @Test
    void modelNonRequiredFieldTest() {
        WebhookCreateUpdatePayload webhookCreateUpdatePayload = new WebhookCreateUpdatePayload();

        assertThat(webhookCreateUpdatePayload.getMessage()).isNull();
        assertThat(webhookCreateUpdatePayload.getMessageMap()).isNull();

        webhookCreateUpdatePayload.addMessageItem("Test");
        webhookCreateUpdatePayload.putMessageMapItem("Test", "Test");

        assertThat(webhookCreateUpdatePayload.getMessage()).isNotNull();
        assertThat(webhookCreateUpdatePayload.getMessageMap()).isNotNull();
    }
}
