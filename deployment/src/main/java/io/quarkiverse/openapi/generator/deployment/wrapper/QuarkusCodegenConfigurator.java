package io.quarkiverse.openapi.generator.deployment.wrapper;

import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.languages.JavaClientCodegen;

public class QuarkusCodegenConfigurator extends CodegenConfigurator {

    public static final String CONFIG_PREFIX_PROP = "quarkusConfigPrefix";

    public QuarkusCodegenConfigurator(final String runtimeConfigPrefix) {
        // immutable properties
        this.setGeneratorName("quarkus");
        this.setTemplatingEngineName("qute");
        this.setLibrary(JavaClientCodegen.MICROPROFILE);
        this.addAdditionalProperty(CONFIG_PREFIX_PROP, runtimeConfigPrefix.replace("\"", "\\\""));
    }

}
