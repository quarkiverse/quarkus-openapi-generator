package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import jakarta.ws.rs.HeaderParam;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ImplicitHeadersTest {

    @Test
    void testImplicitHeadersAllRemoved() {
        String apiClassName = "org.openapi.quarkus.implicit_headers_yaml.api.UserResourceApi";

        try {
            Class<?> apiClass = Class.forName(apiClassName);

            // Test findAll method - should not have any header parameters
            Method findAllMethod = apiClass.getMethod("findAll");
            Parameter[] params = findAllMethod.getParameters();

            // Verify no header parameters exist in the method
            long headerParamCount = Arrays.stream(params)
                    .filter(p -> p.isAnnotationPresent(HeaderParam.class))
                    .count();

            assertThat(headerParamCount)
                    .as("No header parameters should be present when implicit-headers=true")
                    .isEqualTo(0);

            // Test add method - should not have header parameters
            Class<?> userClass = Class.forName("org.openapi.quarkus.implicit_headers_yaml.model.User");
            Method addMethod = apiClass.getMethod("add", userClass);
            Parameter[] addParams = addMethod.getParameters();

            long addHeaderParamCount = Arrays.stream(addParams)
                    .filter(p -> p.isAnnotationPresent(HeaderParam.class))
                    .count();

            assertThat(addHeaderParamCount)
                    .as("No header parameters should be present when implicit-headers=true")
                    .isEqualTo(0);

        } catch (Exception e) {
            throw new AssertionError("Failed to test implicit headers", e);
        }
    }

    @Test
    void testImplicitHeadersRegex() {
        String apiClassName = "org.openapi.quarkus.implicit_headers_regex_yaml.api.UserResourceApi";

        try {
            Class<?> apiClass = Class.forName(apiClassName);
            Class<?> userClass = Class.forName("org.openapi.quarkus.implicit_headers_regex_yaml.model.User");

            // Test findAll method - should have only non-matching header parameters (authorization, custom-header)
            Method findAllMethod = apiClass.getMethod("findAll", String.class, String.class);
            Parameter[] params = findAllMethod.getParameters();

            // Verify header parameters matching x-.* are removed, but others remain
            long headerParamCount = Arrays.stream(params)
                    .filter(p -> p.isAnnotationPresent(HeaderParam.class))
                    .count();

            assertThat(headerParamCount)
                    .as("Only 2 header parameters should remain (authorization, custom-header) when implicit-headers-regex=x-.*")
                    .isEqualTo(2);

            // Verify the remaining headers are the expected ones
            String[] remainingHeaders = Arrays.stream(params)
                    .filter(p -> p.isAnnotationPresent(HeaderParam.class))
                    .map(p -> p.getAnnotation(HeaderParam.class).value())
                    .toArray(String[]::new);

            assertThat(remainingHeaders)
                    .as("Remaining headers should be authorization and custom-header")
                    .containsExactlyInAnyOrder("authorization", "custom-header");

            // Test add method - should have only authorization header (x-request-id removed)
            Method addMethod = apiClass.getMethod("add", String.class, userClass);
            Parameter[] addParams = addMethod.getParameters();

            long addHeaderParamCount = Arrays.stream(addParams)
                    .filter(p -> p.isAnnotationPresent(HeaderParam.class))
                    .count();

            assertThat(addHeaderParamCount)
                    .as("Only 1 header parameter should remain (authorization) when implicit-headers-regex=x-.*")
                    .isEqualTo(1);

            String[] addRemainingHeaders = Arrays.stream(addParams)
                    .filter(p -> p.isAnnotationPresent(HeaderParam.class))
                    .map(p -> p.getAnnotation(HeaderParam.class).value())
                    .toArray(String[]::new);

            assertThat(addRemainingHeaders)
                    .as("Remaining header should be authorization")
                    .containsExactly("authorization");

        } catch (Exception e) {
            throw new AssertionError("Failed to test implicit headers regex", e);
        }
    }
}
