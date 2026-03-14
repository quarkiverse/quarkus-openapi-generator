package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.openapi_json.api.UserManagementApi;
import org.openapi.quarkus.openapi_json.model.MeterUserRole;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

@QuarkusTest
class ReuseEnumsTest {

    @RestClient
    @Inject
    UserManagementApi api;

    @Test
    void verifyReturnType() {
        // This line will fail to compile if the return type is Uni<Set<String>>
        // explicitly declaring the type to force compilation error if mismatch
        Uni<Set<MeterUserRole>> result = api.internalUserUserIdMeterRolesEvaluateMeterMeterIdGet(1L, 1L);
        assertThat(result).isNotNull();
    }
}
