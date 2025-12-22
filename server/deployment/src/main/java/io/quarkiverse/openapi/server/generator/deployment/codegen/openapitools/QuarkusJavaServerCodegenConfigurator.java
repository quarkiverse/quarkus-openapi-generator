package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import static java.lang.Boolean.FALSE;

import java.util.Objects;

import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.languages.JavaClientCodegen;

public class QuarkusJavaServerCodegenConfigurator extends CodegenConfigurator {

    public static final String USE_BEAN_VALIDATION = "use-bean-validation";
    public static final String BASE_PACKAGE = "base-package";

    public QuarkusJavaServerCodegenConfigurator() {
        this.setGeneratorName(QuarkusJavaServerCodegen.CODEGEN_NAME);
        this.setTemplatingEngineName("qute");
        this.setLibrary(JavaClientCodegen.MICROPROFILE);

        // defaults
        this.addAdditionalProperty(USE_BEAN_VALIDATION, FALSE);
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
        this.addAdditionalProperty("base-package", Objects.requireNonNull(basePackage));
        return this;
    }

    public QuarkusJavaServerCodegenConfigurator useBeanValidation(boolean useBeanValidation) {
        this.addAdditionalProperty("use-bean-validation", useBeanValidation);
        return this;
    }

    public QuarkusJavaServerCodegenConfigurator withReactive(boolean reactive) {
        this.addAdditionalProperty("use-reactive", reactive);
        return this;
    }
}
