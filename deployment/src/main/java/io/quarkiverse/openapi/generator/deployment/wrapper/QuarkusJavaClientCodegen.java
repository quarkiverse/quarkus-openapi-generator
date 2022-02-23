package io.quarkiverse.openapi.generator.deployment.wrapper;

import java.io.File;

import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.languages.JavaClientCodegen;

public class QuarkusJavaClientCodegen extends JavaClientCodegen {

    private static final String AUTH_PACKAGE = "auth";

    public QuarkusJavaClientCodegen() {
        // immutable properties
        this.setDateLibrary(JavaClientCodegen.JAVA8_MODE);
        this.setTemplateDir("templates");
        // we are only interested in the main generated classes
        this.projectFolder = "";
        this.projectTestFolder = "";
        this.sourceFolder = "";
        this.testFolder = "";
    }

    @Override
    public String getName() {
        return "quarkus";
    }

    @Override
    public void processOpts() {
        super.processOpts();
        this.replaceWithQuarkusTemplateFiles();
    }

    private void replaceWithQuarkusTemplateFiles() {
        supportingFiles.clear();
        // TODO: verify if we need basic authentication in the given OpenAPI definition
        supportingFiles.add(new SupportingFile("auth/basicAuthenticationProvider.qute", authFileFolder(),
                "BasicAuthenticationProvider.java"));

        apiTemplateFiles.clear();
        apiTemplateFiles.put("api.qute", ".java");

        modelTemplateFiles.clear();
        modelTemplateFiles.put("model.qute", ".java");
    }

    public String authFileFolder() {
        // we are not using the apiFileFolder since it returns the full path
        // we are only interested in the package path
        return apiPackage().replace('.', File.separatorChar) + File.separator + AUTH_PACKAGE;
    }
}
