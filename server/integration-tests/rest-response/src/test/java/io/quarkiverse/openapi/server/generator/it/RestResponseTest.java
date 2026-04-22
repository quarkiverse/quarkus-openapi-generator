package io.quarkiverse.openapi.server.generator.it;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class RestResponseTest {

    @Test
    public void testGeneratedMethodReturnTypes() throws Exception {
        Class<?> resourceClass = Class.forName("org.acme.resources.DefaultResource");

        // Simple type
        Method helloMethod = resourceClass.getMethod("hello");
        Assertions.assertEquals("org.jboss.resteasy.reactive.RestResponse", helloMethod.getReturnType().getName());
        Assertions.assertEquals("java.lang.String",
                ((java.lang.reflect.ParameterizedType) helloMethod.getGenericReturnType()).getActualTypeArguments()[0]
                        .getTypeName());

        // Model type
        Method createMethod = resourceClass.getMethod("create");
        Assertions.assertEquals("org.jboss.resteasy.reactive.RestResponse", createMethod.getReturnType().getName());
        Assertions.assertEquals("org.acme.model.Model",
                ((java.lang.reflect.ParameterizedType) createMethod.getGenericReturnType()).getActualTypeArguments()[0]
                        .getTypeName());

        // Void type
        Method forbiddenMethod = resourceClass.getMethod("forbidden");
        Assertions.assertEquals("org.jboss.resteasy.reactive.RestResponse", forbiddenMethod.getReturnType().getName());
        Assertions.assertEquals("java.lang.Void",
                ((java.lang.reflect.ParameterizedType) forbiddenMethod.getGenericReturnType()).getActualTypeArguments()[0]
                        .getTypeName());

        // List type
        Method listMethod = resourceClass.getMethod("callList");
        Assertions.assertEquals("org.jboss.resteasy.reactive.RestResponse", listMethod.getReturnType().getName());
        Assertions.assertEquals("java.util.List<org.acme.model.Model>",
                ((java.lang.reflect.ParameterizedType) listMethod.getGenericReturnType()).getActualTypeArguments()[0]
                        .getTypeName());

        // Map type
        Method mapMethod = resourceClass.getMethod("map");
        Assertions.assertEquals("org.jboss.resteasy.reactive.RestResponse", mapMethod.getReturnType().getName());
        Assertions.assertEquals("java.util.Map<java.lang.String, org.acme.model.Model>",
                ((java.lang.reflect.ParameterizedType) mapMethod.getGenericReturnType()).getActualTypeArguments()[0]
                        .getTypeName());
    }
}
