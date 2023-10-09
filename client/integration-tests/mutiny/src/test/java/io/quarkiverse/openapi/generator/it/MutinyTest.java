package io.quarkiverse.openapi.generator.it;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.quarkus_simple_openapi_yaml.api.ReactiveGreetingResourceApi;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

@QuarkusTest
@Tag("resteasy-reactive")
class MutinyTest {

    @Test
    void apiIsBeingGenerated() throws NoSuchMethodException {
        assertThat(ReactiveGreetingResourceApi.class.getMethod("hello").getReturnType())
                .isEqualTo(Uni.class);
    }
}
