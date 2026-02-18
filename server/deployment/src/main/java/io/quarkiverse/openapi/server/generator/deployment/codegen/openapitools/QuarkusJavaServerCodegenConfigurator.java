package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import java.util.Objects;

import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.languages.JavaClientCodegen;

public class QuarkusJavaServerCodegenConfigurator extends CodegenConfigurator {

    public static final String BASE_PACKAGE = "base-package";
    public static final String USE_BEAN_VALIDATION = "use-bean-validation";
    public static final String GENERATE_BUILDERS = "use-builders";
    public static final String USE_REACTIVE = "use-reactive";

    public QuarkusJavaServerCodegenConfigurator() {
        this.setGeneratorName(QuarkusJavaServerCodegen.CODEGEN_NAME);
        this.setTemplatingEngineName("qute");
        this.setLibrary(JavaClientCodegen.MICROPROFILE);

        this.addAdditionalProperty(USE_BEAN_VALIDATION, Boolean.FALSE);
        this.addAdditionalProperty(BASE_PACKAGE, "org.acme");
        this.addAdditionalProperty(GENERATE_BUILDERS, Boolean.FALSE);
        this.addAdditionalProperty(USE_REACTIVE, Boolean.FALSE);
    }

    public QuarkusJavaServerCodegenConfigurator withInputBaseDir(String inputSpec) {
        this.setInputSpec(Objects.requireNonNull(inputSpec));
        return this;
    }

    public QuarkusJavaServerCodegenConfigurator withOutputDir(String outputDir) {
        this.setOutputDir(Objects.requireNonNull(outputDir));
        return this;
    }

    public QuarkusJavaServerCodegenConfigurator withBasePackage(String basePackage) {
        this.addAdditionalProperty(BASE_PACKAGE, Objects.requireNonNull(basePackage));
        return this;
    }

    public QuarkusJavaServerCodegenConfigurator withBeanValidation(boolean useBeanValidation) {
        this.addAdditionalProperty(USE_BEAN_VALIDATION, useBeanValidation);
        return this;
    }

    public QuarkusJavaServerCodegenConfigurator withReactive(boolean useReactive) {
        this.addAdditionalProperty(USE_REACTIVE, useReactive);
        return this;
    }

    public QuarkusJavaServerCodegenConfigurator withGenerateBuilders(boolean generateBuilders) {
        this.addAdditionalProperty(GENERATE_BUILDERS, generateBuilders);
        return this;
    }
}
