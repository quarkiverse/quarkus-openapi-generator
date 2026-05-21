package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools.qute;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

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
}
