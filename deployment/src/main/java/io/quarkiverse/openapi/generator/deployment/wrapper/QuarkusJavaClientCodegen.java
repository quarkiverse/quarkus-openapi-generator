package io.quarkiverse.openapi.generator.deployment.wrapper;

import java.io.File;

import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.utils.ProcessUtils;
import org.openapitools.codegen.utils.URLPathUtils;

import io.swagger.v3.oas.models.OpenAPI;

public class QuarkusJavaClientCodegen extends JavaClientCodegen {

    private static final String AUTH_PACKAGE = "auth";
    /**
     * Default server URL (the first one in the OpenAPI spec file servers definition.
     */
    private static final String DEFAULT_SERVER_URL = "defaultServerUrl";

    public QuarkusJavaClientCodegen() {
        // immutable properties
        this.setDateLibrary(JavaClientCodegen.JAVA8_MODE);
        this.setSerializationLibrary(SERIALIZATION_LIBRARY_JACKSON);
        this.setTemplateDir("templates");
    }

    @Override
    public String getName() {
        return "quarkus";
    }

    @Override
    public void processOpts() {
        super.processOpts();
        // we are only interested in the main generated classes
        this.projectFolder = "";
        this.projectTestFolder = "";
        this.sourceFolder = "";
        this.testFolder = "";

        this.replaceWithQuarkusTemplateFiles();
    }

    private void replaceWithQuarkusTemplateFiles() {
        supportingFiles.clear();

        if (ProcessUtils.hasHttpBasicMethods(this.openAPI) ||
                ProcessUtils.hasApiKeyMethods(this.openAPI) ||
                ProcessUtils.hasHttpBearerMethods(this.openAPI) ||
                ProcessUtils.hasOAuthMethods(this.openAPI)) {
            supportingFiles.add(
                    new SupportingFile(AUTH_PACKAGE + "/compositeAuthenticationProvider.qute",
                            authFileFolder(),
                            "CompositeAuthenticationProvider.java"));
            supportingFiles.add(
                    new SupportingFile("auth/headersFactory.qute",
                            authFileFolder(),
                            "AuthenticationHeadersFactory.java"));

        }

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

    @Override
    public void preprocessOpenAPI(OpenAPI openAPI) {
        super.preprocessOpenAPI(openAPI);
        // add the default server url to the context
        additionalProperties.put(DEFAULT_SERVER_URL, URLPathUtils.getServerURL(this.openAPI, serverVariableOverrides()));
    }

    @Override
    public void postProcess() {
        final boolean verbose = Boolean.parseBoolean(GlobalSettings.getProperty(OpenApiClientGeneratorWrapper.VERBOSE));
        if (verbose) {
            super.postProcess();
        }
    }
}
