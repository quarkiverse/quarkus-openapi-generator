package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

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

        // should be configurable in the future
        this.setUseBeanValidation(true);
        this.setPerformBeanValidation(true);

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
