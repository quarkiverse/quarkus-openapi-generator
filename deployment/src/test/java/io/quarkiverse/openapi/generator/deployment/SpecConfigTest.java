package io.quarkiverse.openapi.generator.deployment;

import static io.quarkiverse.openapi.generator.deployment.SpecConfig.BUILD_TIME_SPEC_PREFIX_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class SpecConfigTest {

    @Test
    void verifySpaceEncoding() {
        final String resolvedPrefix = SpecConfig
                .getBuildTimeSpecPropertyPrefix(Path.of("/home/myuser/luke/my test openapi.json"));
        assertEquals(resolvedPrefix, String.format(BUILD_TIME_SPEC_PREFIX_FORMAT, "my%20test%20openapi_json"));
    }

    @Test
    void withSingleFileName() {
        final String resolvedPrefix = SpecConfig.getBuildTimeSpecPropertyPrefix(Path.of("my test openapi.json"));
        assertEquals(resolvedPrefix, String.format(BUILD_TIME_SPEC_PREFIX_FORMAT, "my%20test%20openapi_json"));
    }

}
