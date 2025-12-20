package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import static io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools.QuarkusJavaServerCodegenConfigurator.USE_BEAN_VALIDATION;

import org.openapitools.codegen.languages.JavaClientCodegen;

public class QuarkusJavaServerCodegen extends JavaClientCodegen {

    public static final String CODEGEN_NAME = "quarkus-openapi-generator-server";

    public QuarkusJavaServerCodegen() {
        this.setSerializationLibrary(SERIALIZATION_LIBRARY_JACKSON);
        this.setTemplateDir("templates");
    }

    @Override
    public String getName() {
        return CODEGEN_NAME;
    }

    @Override
    public void processOpts() {
        super.processOpts();

        this.projectFolder = "";
        this.projectTestFolder = "";
        this.sourceFolder = "";
        this.testFolder = "";
        this.embeddedTemplateDir = "templates";
        this.supportsAdditionalPropertiesWithComposedSchema = false;
        this.apiNameSuffix = "Resource";

        // bean validation
        boolean useBeanValidation = (boolean) this.additionalProperties
                .getOrDefault(USE_BEAN_VALIDATION, false);
        this.setUseBeanValidation(useBeanValidation);
        this.setPerformBeanValidation(useBeanValidation);

        String basePackage = (String) this.additionalProperties.get(QuarkusJavaServerCodegenConfigurator.BASE_PACKAGE);
        this.apiPackage = basePackage + ".resources";
        this.modelPackage = basePackage + ".model";

        // replace with Qute templates
        this.applyQute();
    }

    private void applyQute() {
        this.supportingFiles.clear();

        this.apiTemplateFiles.clear();
        this.apiTemplateFiles.put("api.qute", ".java");

        this.modelTemplateFiles.clear();
        this.modelTemplateFiles.put("model.qute", ".java");
    }
}
