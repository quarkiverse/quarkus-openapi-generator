package io.quarkiverse.openapi.server.generator.deployment;

import java.net.URISyntaxException;
import java.nio.file.Path;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.server.generator.deployment.codegen.ApicurioOpenApiServerCodegen;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;

public class CodegenTest {

    @Test
    public void testGeneration() throws CodeGenException, URISyntaxException {
        Config config = MockConfigUtils.getTestConfig("application.properties");

        CodeGenContext codeGenContext = new CodeGenContext(null, Path.of("target/generated-test-sources"),
                Path.of("target/generated-test-sources"), Path.of("generated-test-classes"), false, config, true);

        ApicurioOpenApiServerCodegen apicurioOpenApiServerCodegen = new ApicurioOpenApiServerCodegen();
        apicurioOpenApiServerCodegen.trigger(codeGenContext);
    }
}
