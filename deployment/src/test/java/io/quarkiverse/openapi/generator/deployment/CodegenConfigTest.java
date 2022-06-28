package io.quarkiverse.openapi.generator.deployment;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.BUILD_TIME_SPEC_PREFIX_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class CodegenConfigTest {

    @Test
    void verifyStreamUri() {
        final String resolvedPrefix = CodegenConfig
                .getBuildTimeSpecPropertyPrefix(Path.of("/home/myuser/open-api-stream/luke/my test openapi.json"));
        assertEquals(String.format(BUILD_TIME_SPEC_PREFIX_FORMAT, "luke_my_test_openapi_json"), resolvedPrefix);
    }

    @Test
    void verifySpaceEncoding() {
        final String resolvedPrefix = CodegenConfig
                .getBuildTimeSpecPropertyPrefix(Path.of("/home/myuser/luke/my test openapi.json"));
        assertEquals(String.format(BUILD_TIME_SPEC_PREFIX_FORMAT, "my_test_openapi_json"), resolvedPrefix);
    }

    @Test
    void withSingleFileName() {
        final String resolvedPrefix = CodegenConfig.getBuildTimeSpecPropertyPrefix(Path.of("my test openapi.json"));
        assertEquals(String.format(BUILD_TIME_SPEC_PREFIX_FORMAT, "my_test_openapi_json"), resolvedPrefix);
    }

}
