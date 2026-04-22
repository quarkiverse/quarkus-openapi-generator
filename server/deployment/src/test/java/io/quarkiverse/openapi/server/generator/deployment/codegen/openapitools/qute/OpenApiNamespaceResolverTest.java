package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools.qute;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.model.OperationMap;

class OpenApiNamespaceResolverTest {

    @Test
    void should_return_smallrye_profile_extension_names() {
        Map<String, Object> vendorExtensions = new LinkedHashMap<>();
        vendorExtensions.put("x-smallrye-profile-order", "enabled");
        vendorExtensions.put("x-smallrye-profile-admin", "");
        vendorExtensions.put("x-smallrye-profile-user", "enabled");
        vendorExtensions.put("x-ignored-extension", "ignored");

        List<String> result = OpenApiNamespaceResolver.INSTANCE.smallryeProfileExtensions(vendorExtensions);

        Assertions.assertThat(result)
                .containsExactly("admin", "order", "user");
    }

    @Test
    void should_return_reactive_rest_response_type() {
        Map<String, Object> vendorExtensions = new LinkedHashMap<>();
        vendorExtensions.put("x-codegen-use-rest-response", Boolean.TRUE);

        String result = OpenApiNamespaceResolver.INSTANCE.getMapReturnType(
                "com.example.MyObject",
                Boolean.TRUE,
                Boolean.FALSE,
                vendorExtensions);

        Assertions.assertThat(result)
                .isEqualTo("io.smallrye.mutiny.Uni<org.jboss.resteasy.reactive.RestResponse<com.example.MyObject>>");
    }

    @Test
    void should_return_response_type_for_void_operations_without_reactive() {
        Map<String, Object> vendorExtensions = new LinkedHashMap<>();

        String result = OpenApiNamespaceResolver.INSTANCE.getMapReturnType(
                "void",
                Boolean.FALSE,
                Boolean.FALSE,
                vendorExtensions);

        Assertions.assertThat(result)
                .isEqualTo("jakarta.ws.rs.core.Response");
    }

    @Test
    void should_detect_rest_response_return_types_from_vendor_extensions() {
        CodegenOperation operation = new CodegenOperation();
        operation.returnType = "com.example.MyObject";
        operation.vendorExtensions.put("x-codegen-returnType", "RestResponse");

        OperationMap operations = new OperationMap();
        operations.setOperation(operation);

        Assertions.assertThat(OpenApiNamespaceResolver.INSTANCE.hasRestResponseReturnType(
                operations,
                Boolean.FALSE,
                Boolean.FALSE))
                .isTrue();
    }
}
