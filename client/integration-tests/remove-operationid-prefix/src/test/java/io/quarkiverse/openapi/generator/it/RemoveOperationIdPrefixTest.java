package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class RemoveOperationIdPrefixTest {

    @Test
    void removeOperationIdPrefixTest() {
        String apiClassName = "org.openapi.quarkus.openapi_remove_operation_id_prefix_yaml.api.UserResourceApi";
        String modelClassName = "org.openapi.quarkus.openapi_remove_operation_id_prefix_yaml.model.User";

        assertThatCode(() -> Class.forName(apiClassName).getMethod("find", Integer.class))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("findAll"))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("add", Class.forName(modelClassName)))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("update", Integer.class, Class.forName(modelClassName)))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("delete", Integer.class))
                .doesNotThrowAnyException();

    }

    @Test
    void removeOperationIdPrefixDelimiterTest() {
        String apiClassName = "org.openapi.quarkus.openapi_prefix_delimiter_yaml.api.UserResourceApi";
        String modelClassName = "org.openapi.quarkus.openapi_prefix_delimiter_yaml.model.User";

        assertThatCode(() -> Class.forName(apiClassName).getMethod("find", Integer.class))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("findAll"))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("add", Class.forName(modelClassName)))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("update", Integer.class, Class.forName(modelClassName)))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("delete", Integer.class))
                .doesNotThrowAnyException();

    }

    @Test
    void removeOperationIdPrefixCountTest() {
        String apiClassName = "org.openapi.quarkus.openapi_prefix_count_yaml.api.UserResourceApi";
        String modelClassName = "org.openapi.quarkus.openapi_prefix_count_yaml.model.User";

        assertThatCode(() -> Class.forName(apiClassName).getMethod("find", Integer.class))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("findAll"))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("add", Class.forName(modelClassName)))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("update", Integer.class, Class.forName(modelClassName)))
                .doesNotThrowAnyException();

        assertThatCode(() -> Class.forName(apiClassName).getMethod("delete", Integer.class))
                .doesNotThrowAnyException();

    }
}
