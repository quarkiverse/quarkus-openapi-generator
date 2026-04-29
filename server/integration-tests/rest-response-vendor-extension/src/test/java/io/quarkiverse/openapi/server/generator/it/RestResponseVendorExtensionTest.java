package io.quarkiverse.openapi.server.generator.it;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RestResponseVendorExtensionTest {

    @Test
    public void testGeneratedMethodReturnTypes() throws Exception {
        Class<?> resourceClass = Class.forName("org.acme.resources.DefaultResource");

        // Standard operation (no vendor extension, global false) -> returns String
        Method standardMethod = resourceClass.getMethod("standard");
        Assertions.assertEquals("java.lang.String", standardMethod.getReturnType().getName());

        // Special operation (with vendor extension use-rest-response) -> returns RestResponse<String>
        Method specialMethod = resourceClass.getMethod("special");
        Assertions.assertEquals("org.jboss.resteasy.reactive.RestResponse", specialMethod.getReturnType().getName());

        // Return type extension (x-codegen-returnType: RestResponse) -> returns RestResponse<String>
        Method returnTypeExtMethod = resourceClass.getMethod("returnTypeExt");
        Assertions.assertEquals("org.jboss.resteasy.reactive.RestResponse", returnTypeExtMethod.getReturnType().getName());

        Path generatedSource = Path.of("target", "generated-sources", "quarkus-openapi-generator-server", "org", "acme",
                "resources", "DefaultResource.java");
        String source = Files.readString(generatedSource);

        Assertions.assertTrue(source.contains("import org.jboss.resteasy.reactive.RestResponse;"),
                "Generated source must import RestResponse when any operation returns it");
        Assertions.assertTrue(source.contains("RestResponse<String>"),
                "Generated source should preserve the generic RestResponse payload type");
    }
}
