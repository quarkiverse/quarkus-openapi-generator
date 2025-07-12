package io.quarkiverse.openapi.server.generator.it;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.devtools.codestarts.quarkus.QuarkusCodestartCatalog;
import io.quarkus.devtools.testing.codestarts.QuarkusCodestartTest;

public class QuarkusOpenAPIGeneratorServerCodestartsTest {

    @RegisterExtension
    public static QuarkusCodestartTest codestartTest = QuarkusCodestartTest.builder()
            .languages(QuarkusCodestartCatalog.Language.JAVA)
            .setupStandaloneExtensionTest("io.quarkiverse.openapi.generator:quarkus-openapi-generator-server").build();

    @Test
    void testContent() throws Throwable {
        codestartTest
                .assertThatGeneratedFile(QuarkusCodestartCatalog.Language.JAVA, "src/main/resources/application.properties")
                .content()
                .contains("quarkus.openapi.generator.spec=openapi.yml");
        codestartTest
                .assertThatGeneratedFile(QuarkusCodestartCatalog.Language.JAVA, "src/main/resources/openapi/openapi.yml")
                .content()
                .contains("title: Generated API");
    }
}
