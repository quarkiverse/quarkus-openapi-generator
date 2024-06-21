package io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PathTest {

    @Test
    void should_create_a_simple_path_when_there_is_no_properties() {
        // given
        String ping = "/ping";

        // when
        Path path = Path.create(ping);

        // then
        Assertions.assertEquals("/ping", path.getValue());
    }
}