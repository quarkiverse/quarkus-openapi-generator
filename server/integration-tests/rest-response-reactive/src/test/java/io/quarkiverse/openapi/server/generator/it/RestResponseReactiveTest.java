package io.quarkiverse.openapi.server.generator.it;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class RestResponseReactiveTest {

    @Test
    public void testGeneratedMethodReturnTypes() throws Exception {
        Class<?> resourceClass = Class.forName("org.acme.resources.DefaultResource");

        // Simple type
        Method helloMethod = resourceClass.getMethod("hello");
        Assertions.assertEquals("io.smallrye.mutiny.Uni", helloMethod.getReturnType().getName());
        Assertions.assertEquals("org.jboss.resteasy.reactive.RestResponse<java.lang.String>",
                ((java.lang.reflect.ParameterizedType) helloMethod.getGenericReturnType()).getActualTypeArguments()[0]
                        .getTypeName());

        // Model type
        Method createMethod = resourceClass.getMethod("create");
        Assertions.assertEquals("io.smallrye.mutiny.Uni", createMethod.getReturnType().getName());
        Assertions.assertEquals("org.jboss.resteasy.reactive.RestResponse<org.acme.model.Model>",
                ((java.lang.reflect.ParameterizedType) createMethod.getGenericReturnType()).getActualTypeArguments()[0]
                        .getTypeName());

        // Void type
        Method forbiddenMethod = resourceClass.getMethod("forbidden");
        Assertions.assertEquals("io.smallrye.mutiny.Uni", forbiddenMethod.getReturnType().getName());
        Assertions.assertEquals("org.jboss.resteasy.reactive.RestResponse<java.lang.Void>",
                ((java.lang.reflect.ParameterizedType) forbiddenMethod.getGenericReturnType()).getActualTypeArguments()[0]
                        .getTypeName());

        // List type
        Method listMethod = resourceClass.getMethod("callList");
        Assertions.assertEquals("io.smallrye.mutiny.Uni", listMethod.getReturnType().getName());
        Assertions.assertEquals("org.jboss.resteasy.reactive.RestResponse<java.util.List<org.acme.model.Model>>",
                ((java.lang.reflect.ParameterizedType) listMethod.getGenericReturnType()).getActualTypeArguments()[0]
                        .getTypeName());

        // Map type
        Method mapMethod = resourceClass.getMethod("map");
        Assertions.assertEquals("io.smallrye.mutiny.Uni", mapMethod.getReturnType().getName());
        Assertions.assertEquals(
                "org.jboss.resteasy.reactive.RestResponse<java.util.Map<java.lang.String, org.acme.model.Model>>",
                ((java.lang.reflect.ParameterizedType) mapMethod.getGenericReturnType()).getActualTypeArguments()[0]
                        .getTypeName());
    }
}
