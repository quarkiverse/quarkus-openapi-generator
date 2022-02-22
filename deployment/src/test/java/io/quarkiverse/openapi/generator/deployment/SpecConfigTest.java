package io.quarkiverse.openapi.generator.deployment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class SpecConfigTest {

    @Test
    void verifySpaceEncoding() {
        final String resolvedBasePackageProperty = SpecConfig
                .getResolvedBasePackageProperty(Path.of("/home/myuser/luke/my test openapi.json"));
        assertEquals(resolvedBasePackageProperty,
                String.format(SpecConfig.BASE_PACKAGE_PROP_FORMAT, "my%20test%20openapi.json"));
    }

    @Test
    void withSingleFileName() {
        final String resolvedBasePackageProperty = SpecConfig.getResolvedBasePackageProperty(Path.of("my test openapi.json"));
        assertEquals(resolvedBasePackageProperty,
                String.format(SpecConfig.BASE_PACKAGE_PROP_FORMAT, "my%20test%20openapi.json"));
    }

}
