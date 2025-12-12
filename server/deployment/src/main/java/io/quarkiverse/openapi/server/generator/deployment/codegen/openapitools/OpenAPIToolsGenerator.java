package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.GlobalSettings;

public class OpenAPIToolsGenerator {

    private QuarkusJavaServerCodegenConfigurator configurator;
    private final DefaultGenerator generator;

    public OpenAPIToolsGenerator(QuarkusJavaServerCodegenConfigurator configurator) {
        this.configurator = configurator;

        applyGlobalSettings();
        applyDefaults();

        this.generator = new DefaultGenerator();
    }

    private void applyGlobalSettings() {
        GlobalSettings.setProperty(CodegenConstants.API_DOCS, FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.API_TESTS, FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.MODEL_TESTS, FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.MODEL_DOCS, FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.APIS, "");
        GlobalSettings.setProperty(CodegenConstants.MODELS, "");
        GlobalSettings.setProperty(CodegenConstants.SUPPORTING_FILES, "");

        // mut be configured in the future
        GlobalSettings.setProperty("verbose", FALSE.toString());
    }

    private void applyDefaults() {

        this.configurator.addAdditionalProperty("classes-codegen", new HashMap<>());
        this.configurator.addAdditionalProperty("additionalApiTypeAnnotations", new String[0]);
        this.configurator.addAdditionalProperty("additionalPropertiesAsAttribute", FALSE);
        this.configurator.addAdditionalProperty("initializeEmptyCollections", FALSE);
        this.configurator.addAdditionalProperty("additionalEnumTypeUnexpectedMember", FALSE);
        this.configurator.addAdditionalProperty("additionalEnumTypeUnexpectedMemberName", "");
        this.configurator.addAdditionalProperty("additionalEnumTypeUnexpectedMemberStringValue", "");
        this.configurator.addAdditionalProperty("additionalRequestArgs", new String[0]);
        this.configurator.addAdditionalProperty("classes-codegen", new HashMap<>());
        this.configurator.addAdditionalProperty("circuit-breaker", new HashMap<>());
        this.configurator.addAdditionalProperty("configKey", "");
        this.configurator.addAdditionalProperty("datatypeWithEnum", "");
        this.configurator.addAdditionalProperty("enable-security-generation", TRUE);
        this.configurator.addAdditionalProperty("generate-part-filename", FALSE);
        this.configurator.addAdditionalProperty("mutiny", FALSE);
        this.configurator.addAdditionalProperty("mutiny-operation-ids", new HashMap<>());
        this.configurator.addAdditionalProperty("mutiny-return-response", FALSE);
        this.configurator.addAdditionalProperty("part-filename-value", "");
        this.configurator.addAdditionalProperty("return-response", FALSE);
        this.configurator.addAdditionalProperty("skipFormModel", TRUE);
        this.configurator.addAdditionalProperty("templateDir", "");
        this.configurator.addAdditionalProperty("use-bean-validation", FALSE);
        this.configurator.addAdditionalProperty("use-field-name-in-part-filename", FALSE);
        this.configurator.addAdditionalProperty("verbose", FALSE);
        this.configurator.addAdditionalProperty(CodegenConstants.SERIALIZABLE_MODEL, FALSE);
        this.configurator.addAdditionalProperty("equals-hashcode", TRUE);
        this.configurator.addAdditionalProperty("use-dynamic-url", FALSE);
    }

    public List<File> generate() {
        return generator.opts(configurator.toClientOptInput()).generate();
    }

}
