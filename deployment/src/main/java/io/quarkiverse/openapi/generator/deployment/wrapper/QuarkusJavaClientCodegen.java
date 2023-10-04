package io.quarkiverse.openapi.generator.deployment.wrapper;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.utils.ProcessUtils;
import org.openapitools.codegen.utils.URLPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

public class QuarkusJavaClientCodegen extends JavaClientCodegen {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusJavaClientCodegen.class);

    public static final String QUARKUS_GENERATOR_NAME = "quarkus-generator";

    private static final String AUTH_PACKAGE = "auth";
    /*
     * Default server URL (the first one in the OpenAPI spec file servers definition.
     */
    private static final String DEFAULT_SERVER_URL = "defaultServerUrl";

    public QuarkusJavaClientCodegen() {
        // immutable properties
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
        this.embeddedTemplateDir = "templates";

        this.replaceWithQuarkusTemplateFiles();
    }

    private void replaceWithQuarkusTemplateFiles() {
        supportingFiles.clear();

        Boolean enableSecurityGeneration = (Boolean) this.additionalProperties.get("enable-security-generation");

        if (enableSecurityGeneration == null || enableSecurityGeneration) {
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
                                "AuthenticationPropagationHeadersFactory.java"));

            }
        } else {
            LOGGER.info("Generating of security classes is disabled!");
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

    public static Optional<URL> getServerURL(OpenAPI openAPI, Map<String, String> userDefinedVariables) {
        final List<Server> servers = openAPI.getServers();
        if (servers == null || servers.isEmpty()) {
            return Optional.empty();
        }
        final Server server = servers.get(0);
        return server.getUrl().equals("/") ? Optional.empty()
                : Optional.ofNullable(URLPathUtils.getServerURL(server, userDefinedVariables));
    }

    @Override
    public void preprocessOpenAPI(OpenAPI openAPI) {
        super.preprocessOpenAPI(openAPI);
        // add the default server url to the context
        getServerURL(this.openAPI, serverVariableOverrides())
                .ifPresent(url -> additionalProperties.put(DEFAULT_SERVER_URL, url));
        additionalProperties.put(OpenApiClientGeneratorWrapper.DEFAULT_SECURITY_SCHEME,
                GlobalSettings.getProperty(OpenApiClientGeneratorWrapper.DEFAULT_SECURITY_SCHEME));

        this.configureAdditionalPropertiesAsAttribute();
    }

    @Override
    public void postProcess() {
        final boolean verbose = Boolean.parseBoolean(GlobalSettings.getProperty(OpenApiClientGeneratorWrapper.VERBOSE));
        if (verbose) {
            super.postProcess();
        }

    }

    @Override
    public String toEnumVarName(String value, String datatype) {

        if (value.isBlank()) {
            return "EMPTY";
        }

        if (this.getSymbolName(value) != null) {
            return this.getSymbolName(value).toUpperCase(Locale.ROOT);
        }

        String enumVarName = super.toEnumVarName(value, datatype);

        if (enumVarName.startsWith("NUMBER_")) {
            return enumVarName;
        }

        Map<Integer, String> indexesOfSpecialChars = new TreeMap<>();
        for (String key : this.specialCharReplacements.keySet()) {
            // no consider underscore from super result
            if (Objects.equals(key, "_")) {
                continue;
            }

            int index = value.indexOf(key);
            while (index != -1) {
                indexesOfSpecialChars.put(index, key);
                index = value.indexOf(key, index + key.length());
            }
        }

        for (String specialChar : indexesOfSpecialChars.values()) {
            enumVarName = enumVarName.replaceFirst("_", this.specialCharReplacements.get(specialChar));
        }

        for (String specialChar : this.specialCharReplacements.values()) {
            if (enumVarName.contains(specialChar)) {
                enumVarName = enumVarName.replace(specialChar, "_" + specialChar + "_");
            }
        }

        // remove _ at start and end
        enumVarName = enumVarName.replaceAll("^_+|_+$", "");
        enumVarName = enumVarName.replaceFirst("\\d.*", "_".concat(enumVarName));

        return enumVarName.toUpperCase(Locale.ROOT);
    }

    @Override
    protected String getSymbolName(String input) {
        String symbolName = this.specialCharReplacements.get(input);
        return symbolName != null ? symbolName.concat("_symbol") : null;
    }

    private void configureAdditionalPropertiesAsAttribute() {
        String property = GlobalSettings.getProperty(OpenApiClientGeneratorWrapper.SUPPORTS_ADDITIONAL_PROPERTIES_AS_ATTRIBUTE);
        if (Boolean.parseBoolean(property)) {
            this.supportsAdditionalPropertiesWithComposedSchema = true;
        }
    }
}
