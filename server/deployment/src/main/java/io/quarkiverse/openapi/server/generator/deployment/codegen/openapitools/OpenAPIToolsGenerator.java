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
        GlobalSettings.setProperty("verbose", FALSE.toString());
    }

    private void applyDefaults() {
        this.configurator.addAdditionalProperty("datatypeWithEnum", "");
        this.configurator.addAdditionalProperty("dateLibrary", "java8");
        // extension additional properties
        this.configurator.addAdditionalProperty("additional-properties-as-attribute", FALSE);
        this.configurator.addAdditionalProperty("classes-codegen", new HashMap<>());
        this.configurator.addAdditionalProperty("equals-hashcode", TRUE);
        this.configurator.addAdditionalProperty("initialize-empty-collections", TRUE);
        this.configurator.addAdditionalProperty(CodegenConstants.SERIALIZABLE_MODEL, FALSE);
    }

    public List<File> generate() {
        return generator.opts(configurator.toClientOptInput()).generate();
    }

}
