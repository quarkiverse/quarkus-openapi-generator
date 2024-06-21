package io.quarkiverse.openapi.wiremock.generator.deployment.wrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.file.PathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.bootstrap.prebuild.CodeGenException;

class OpenApiWiremockGeneratorWrapperTest {

    @Test
    @DisplayName("Should not generate Wiremock Stubbing when the input dir is not a directory")
    void should_not_generate_wiremock_stubbing_when_the_input_dir_is_not_a_directory() throws IOException, CodeGenException {
        // given
        Path inputDir = Files.createTempFile("openapi", "wiremock");
        Path outputDir = Files.createTempDirectory("openapi_test_output_dir");
        OpenApiWiremockGeneratorWrapper openApiWiremock = new OpenApiWiremockGeneratorWrapper(
                inputDir,
                outputDir);

        // when
        openApiWiremock.generate();

        // then
        Assertions.assertTrue(PathUtils.isEmpty(outputDir));
    }

    @Test
    @DisplayName("Should not generate Wiremock Stubbing when the input dir is empty")
    void should_not_generate_wiremock_stubbing_when_the_input_dir_is_empty() throws IOException, CodeGenException {
        // given
        Path inputDir = Files.createTempDirectory("openapi_test_input_dir");
        Path outputDir = Files.createTempDirectory("openapi_test_output_dir");
        OpenApiWiremockGeneratorWrapper openApiWiremock = new OpenApiWiremockGeneratorWrapper(
                inputDir,
                outputDir);

        // when
        openApiWiremock.generate();

        // then
        Assertions.assertTrue(PathUtils.isEmpty(outputDir));
    }
}