package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.languages.JavaClientCodegen;

public class QuarkusJavaServerCodegenConfigurator extends CodegenConfigurator {

    public QuarkusJavaServerCodegenConfigurator() {
        this.setGeneratorName(QuarkusJavaServerCodegen.CODEGEN_NAME);
        this.setTemplatingEngineName("qute");
        this.setLibrary(JavaClientCodegen.MICROPROFILE);
    }

}
