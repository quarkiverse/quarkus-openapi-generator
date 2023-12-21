package io.quarkiverse.openapi.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.devtools.codestarts.quarkus.QuarkusCodestartCatalog;
import io.quarkus.devtools.testing.codestarts.QuarkusCodestartTest;

public class QuarkusOpenApiGeneratorCodestartTest {

    @RegisterExtension
    QuarkusCodestartTest codestartTest = QuarkusCodestartTest.builder()
            .languages(QuarkusCodestartCatalog.Language.JAVA)
            .setupStandaloneExtensionTest("io.quarkiverse.openapi.generator:quarkus-openapi-generator")
            .build();

    @Test
    void assertThatOpenApiFileIsGenerated() throws Throwable {
        codestartTest.assertThatGeneratedFileMatchSnapshot(QuarkusCodestartCatalog.Language.JAVA,
                "src/main/openapi/openapi.yaml");
    }

}
