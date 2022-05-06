package io.quarkiverse.openapi.generator.deployment;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.BUILD_TIME_SPEC_PREFIX_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class CodegenConfigTest {

    @Test
    void verifySpaceEncoding() {
        final String resolvedPrefix = CodegenConfig
                .getBuildTimeSpecPropertyPrefix(Path.of("/home/myuser/luke/my test openapi.json"));
        assertEquals(resolvedPrefix, String.format(BUILD_TIME_SPEC_PREFIX_FORMAT, "my_test_openapi_json"));
    }

    @Test
    void withSingleFileName() {
        final String resolvedPrefix = CodegenConfig.getBuildTimeSpecPropertyPrefix(Path.of("my test openapi.json"));
        assertEquals(resolvedPrefix, String.format(BUILD_TIME_SPEC_PREFIX_FORMAT, "my_test_openapi_json"));
    }

}
