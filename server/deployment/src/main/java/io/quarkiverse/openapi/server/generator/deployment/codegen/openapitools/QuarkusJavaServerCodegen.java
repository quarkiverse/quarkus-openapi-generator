package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import static io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools.QuarkusJavaServerCodegenConfigurator.USE_BEAN_VALIDATION;
import static io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools.QuarkusJavaServerCodegenConfigurator.USE_REST_RESPONSE;

import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.utils.ModelUtils;

import io.swagger.v3.oas.models.media.Schema;

public class QuarkusJavaServerCodegen extends JavaClientCodegen {

    public static final String CODEGEN_NAME = "quarkus-openapi-generator-server";

    public QuarkusJavaServerCodegen() {
        this.setSerializationLibrary(SERIALIZATION_LIBRARY_JACKSON);
        this.setTemplateDir("server-templates");
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

        // rest response
        boolean useRestResponse = (boolean) this.additionalProperties
                .getOrDefault(USE_REST_RESPONSE, false);
        this.additionalProperties.put("use-rest-response", useRestResponse);

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

    @Override
    public CodegenProperty fromProperty(String name, Schema p, boolean required, boolean schemaIsFromAdditionalProperties) {
        if (p != null && p.getType() != null) {
            if ("object".equals(p.getType()) && p.getDefault() != null && p.getAdditionalProperties() == null
                    && p.getItems() == null) {
                p.setAdditionalProperties(true);
            }
        }
        return super.fromProperty(name, p, required, schemaIsFromAdditionalProperties);
    }

    @Override
    public String toDefaultValue(CodegenProperty property, Schema schema) {
        Schema referencedSchema = ModelUtils.getReferencedSchema(this.openAPI, schema);
        if (ModelUtils.isObjectSchema(referencedSchema) && referencedSchema.getDefault() != null) {
            return null;
        }
        return super.toDefaultValue(property, schema);
    }
}
