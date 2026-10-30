package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Set;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.openapi_json.api.UserManagementApi;
import org.openapi.quarkus.openapi_json.model.MeterUserRole;
import org.openapi.quarkus.openapi_json.model.UserRoleAssignment;

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

    @Test
    void verifyReturnTypeWith2xxWildcard() {
        // Verifies that 2XX wildcard response codes are handled the same as numeric 200-299
        // This line will fail to compile if the return type is not properly resolved to Uni<Set<MeterUserRole>>
        Uni<Set<MeterUserRole>> result = api.internalUserUserIdMeterRolesEvaluateMeterMeterIdWildcardGet(1L, 1L);
        assertThat(result).isNotNull();
    }

    @Test
    void verifyInlineEnumPropertyReusesTopLevelEnum() {
        // The role property is declared as an inline enum in the spec. With reuse-enums enabled,
        // it must be generated as the shared top-level MeterUserRole type instead of a nested enum.
        // This line will fail to compile if the property type is a nested RoleEnum
        UserRoleAssignment assignment = new UserRoleAssignment();
        assignment.setRole(MeterUserRole.ADMIN);
        MeterUserRole role = assignment.getRole();
        assertThat(role).isEqualTo(MeterUserRole.ADMIN);

        // No nested enum type should have been generated for the inline enum property
        assertThat(Arrays.stream(UserRoleAssignment.class.getDeclaredClasses()).anyMatch(Class::isEnum)).isFalse();
    }
}
