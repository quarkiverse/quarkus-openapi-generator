package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.Path;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PathTest {

    @Test
    void testPathGenerated() throws ClassNotFoundException, NoSuchMethodException {
        String apiClassName = "org.openapi.quarkus.openapi_path_yaml.api.UserResourceApi";
        String modelClassName = "org.openapi.quarkus.openapi_path_yaml.model.User";

        assertThat(
                Class.forName(apiClassName)
                        .getAnnotation(Path.class)
                        .value())
                .isEqualTo("/users");

        assertThat(
                Class.forName(apiClassName)
                        .getMethod("userResourceFind", Integer.class)
                        .getAnnotation(Path.class)
                        .value())
                .isEqualTo("/{id}");

        assertThat(
                Class.forName(apiClassName)
                        .getMethod("userResourceFindAll")
                        .getAnnotation(Path.class))
                .isNull();

        assertThat(
                Class.forName(apiClassName)
                        .getMethod("userResourceAdd", Class.forName(modelClassName))
                        .getAnnotation(Path.class))
                .isNull();

        assertThat(
                Class.forName(apiClassName)
                        .getMethod("userResourceUpdate", Integer.class, Class.forName(modelClassName))
                        .getAnnotation(Path.class)
                        .value())
                .isEqualTo("/{id}");

        assertThat(
                Class.forName(apiClassName)
                        .getMethod("userResourceDelete", Integer.class)
                        .getAnnotation(Path.class)
                        .value())
                .isEqualTo("/{id}");
    }

    @Test
    void testPathWithForwardSlashGenerated() throws ClassNotFoundException, NoSuchMethodException {
        String apiClassName = "org.openapi.quarkus.openapi_path_with_forward_slash_yaml.api.UserResourceApi";
        String modelClassName = "org.openapi.quarkus.openapi_path_with_forward_slash_yaml.model.User";

        assertThat(
                Class.forName(apiClassName)
                        .getAnnotation(Path.class)
                        .value())
                .isEqualTo("/users");

        assertThat(
                Class.forName(apiClassName)
                        .getMethod("userResourceFind", Integer.class)
                        .getAnnotation(Path.class)
                        .value())
                .isEqualTo("/{id}");

        assertThat(
                Class.forName(apiClassName)
                        .getMethod("userResourceFindAll")
                        .getAnnotation(Path.class)
                        .value())
                .isEqualTo("/");

        assertThat(
                Class.forName(apiClassName)
                        .getMethod("userResourceAdd", Class.forName(modelClassName))
                        .getAnnotation(Path.class)
                        .value())
                .isEqualTo("/");

        assertThat(
                Class.forName(apiClassName)
                        .getMethod("userResourceUpdate", Integer.class, Class.forName(modelClassName))
                        .getAnnotation(Path.class)
                        .value())
                .isEqualTo("/{id}");

        assertThat(
                Class.forName(apiClassName)
                        .getMethod("userResourceDelete", Integer.class)
                        .getAnnotation(Path.class)
                        .value())
                .isEqualTo("/{id}");
    }

}
