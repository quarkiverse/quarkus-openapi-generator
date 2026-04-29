package io.quarkiverse.openapi.generator.deployment.wrapper;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationMap;
import org.openapitools.codegen.model.OperationsMap;
import org.openapitools.codegen.utils.ProcessUtils;
import org.openapitools.codegen.utils.URLPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.deployment.template.MediaTypeExtensions;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;

public class QuarkusJavaClientCodegen extends JavaClientCodegen {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusJavaClientCodegen.class);

    public static final String QUARKUS_GENERATOR_NAME = "quarkus-generator";

    private static final String AUTH_PACKAGE = "auth";
    /*
     * Default server URL (the first one in the OpenAPI spec file servers definition.
     */
    private static final String DEFAULT_SERVER_URL = "defaultServerUrl";
    private static final String X_MULTI_SEGMENT = "x-multi-segment";
    private static final String X_MULTI_SEGMENT_PARAMS = "x-multi-segment-params";

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
        this.supportsAdditionalPropertiesWithComposedSchema = false;

        Boolean beanValidation = (Boolean) this.additionalProperties.getOrDefault("use-bean-validation", false);

        this.setUseBeanValidation(beanValidation);
        this.setPerformBeanValidation(beanValidation);

        this.replaceWithQuarkusTemplateFiles();
    }

    private void replaceWithQuarkusTemplateFiles() {
        supportingFiles.clear();

        Boolean enableSecurityGeneration = (Boolean) this.additionalProperties.get("enable-security-generation");
        Boolean generateApis = (Boolean) this.additionalProperties.getOrDefault("generate-apis", Boolean.TRUE);
        Boolean generateModels = (Boolean) this.additionalProperties.getOrDefault("generate-models", Boolean.TRUE);

        if (enableSecurityGeneration == null || enableSecurityGeneration) {
            if (ProcessUtils.hasHttpBasicMethods(this.openAPI) ||
                    ProcessUtils.hasApiKeyMethods(this.openAPI) ||
                    ProcessUtils.hasHttpBearerMethods(this.openAPI) ||
                    ProcessUtils.hasOAuthMethods(this.openAPI) ||
                    ProcessUtils.hasOpenIdConnectMethods(this.openAPI)) {
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
        if (generateApis) {
            apiTemplateFiles.put("api.qute", ".java");
        }

        modelTemplateFiles.clear();
        if (generateModels) {
            modelTemplateFiles.put("model.qute", ".java");
        }
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
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);
        if ("set".equals(property.containerType)) {
            model.imports.add("Arrays");
        }
    }

    @Override
    public CodegenModel fromModel(String name, Schema model) {
        CodegenModel codegenModel = super.fromModel(name, model);
        warnIfDuplicated(codegenModel);
        return codegenModel;
    }

    @Override
    public CodegenProperty fromProperty(String name, Schema p, boolean required, boolean schemaIsFromAdditionalProperties) {
        if (p != null && p.getType() != null) {
            // Property is a `type: object` without `additionalProperties: true`, without `properties`, but has `default` values set!
            // In this peculiar situation, the template will try to initialize a Java Object with such values, and it will fail to compile.
            // See https://github.com/quarkiverse/quarkus-openapi-generator/issues/1185 for more context.
            if ("object".equals(p.getType()) && p.getDefault() != null && p.getAdditionalProperties() == null
                    && p.getItems() == null) {
                p.setAdditionalProperties(true);
            }
        }
        return super.fromProperty(name, p, required, schemaIsFromAdditionalProperties);
    }

    private void warnIfDuplicated(CodegenModel m) {
        Set<String> propertyNames = new TreeSet<>();
        for (CodegenProperty element : m.allVars) {
            if (element.deprecated) {
                continue;
            }

            // We can have baseName as `my-type` and `myType`, that are duplicates
            if (propertyNames.contains(element.name)) {
                LOGGER.warn(
                        "Variable {} is duplicated in the OpenAPI spec file. Consider adding the 'deprecated' attribute to it or remove it from the file. Java class {} will fail to compile.",
                        element.baseName, m.classFilename);
            } else {
                propertyNames.add(element.name);
            }
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

        String enumVarName = super.toEnumVarName(value.toLowerCase(Locale.ROOT), datatype);

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
        enumVarName = enumVarName.replaceFirst("^(\\d).*", "_".concat(enumVarName));

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

    @Override
    public OperationsMap postProcessOperationsWithModels(OperationsMap objs, List<ModelMap> allModels) {
        OperationsMap result = super.postProcessOperationsWithModels(objs, allModels);

        OperationMap ops = result.getOperations();
        if (ops != null) {
            List<CodegenOperation> operations = ops.getOperation();
            if (operations != null) {
                for (CodegenOperation operation : operations) {
                    handleMultiSegmentParams(operation);
                    handleReturnType(operation, result);
                }
            }
        }

        return result;
    }

    private Operation findOperation(String path, String httpMethod) {
        if (this.openAPI == null)
            return null;
        PathItem pathItem = this.openAPI.getPaths().get(path);
        if (pathItem == null)
            return null;
        try {
            HttpMethod method = HttpMethod
                    .valueOf(httpMethod.toUpperCase());
            return pathItem.readOperationsMap().get(method);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String findMatchingEnumModel(List<?> enumValues) {
        if (this.openAPI != null && this.openAPI.getComponents() != null
                && this.openAPI.getComponents().getSchemas() != null) {
            for (Map.Entry<String, Schema> entry : this.openAPI.getComponents().getSchemas().entrySet()) {
                Schema s = entry.getValue();
                if (s.getEnum() != null && s.getEnum().equals(enumValues)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private void handleReturnType(CodegenOperation operation, OperationsMap result) {

        if ("String".equals(operation.returnBaseType)
                && ("Set".equals(operation.returnContainer) || "List".equals(operation.returnContainer))) {
            String commonPath = (String) result.get("commonPath");
            String path = getPath(operation, commonPath);

            Operation openApiOperation = findOperation(path, operation.httpMethod);
            if (openApiOperation == null && path != null && path.endsWith("/")) {
                String pathNoSlash = path.substring(0, path.length() - 1);
                openApiOperation = findOperation(pathNoSlash, operation.httpMethod);
            }

            if (openApiOperation != null) {
                if (openApiOperation.getResponses() != null && openApiOperation.getResponses().get("200") != null
                        && openApiOperation.getResponses().get("200").getContent() != null && openApiOperation
                                .getResponses().get("200").getContent().get("application/json") != null) {
                    Schema<?> responseSchema = openApiOperation.getResponses().get("200").getContent()
                            .get("application/json").getSchema();

                    if (responseSchema instanceof ArraySchema) {
                        Schema<?> items = responseSchema.getItems();
                        if (items.getEnum() != null && items.get$ref() == null) {
                            String matchedModel = findMatchingEnumModel(items.getEnum());
                            if (matchedModel != null) {
                                operation.returnBaseType = matchedModel;
                                operation.returnType = operation.returnContainer + "<" + matchedModel + ">";
                                operation.imports.add(matchedModel);

                                // Add to file-level imports
                                List<Map<String, String>> imports = (List<Map<String, String>>) result
                                        .get("imports");
                                Map<String, String> importMap = new java.util.HashMap<>();
                                importMap.put("import", modelPackage() + "." + matchedModel);
                                imports.add(importMap);
                            }
                        }
                    }
                }
            }
        }
    }

    private static String getPath(CodegenOperation operation, String commonPath) {
        String path = operation.path;
        if (commonPath != null && path != null) {
            if (path.startsWith("/")) {
                path = commonPath + path;
            } else {
                path = commonPath + "/" + path;
            }
        } else if (commonPath != null) {
            path = commonPath;
        }
        // Handle double slashes if any
        if (path != null) {
            path = path.replaceAll("//", "/");
        }
        return path;
    }

    private static void handleMultiSegmentParams(CodegenOperation operation) {
        // Build list of multi-segment parameter names
        List<String> multiSegmentParamNames = new ArrayList<>();
        if (operation.pathParams != null) {
            for (CodegenParameter param : operation.pathParams) {
                Object multiSegmentFlag = param.vendorExtensions.get(X_MULTI_SEGMENT);
                if (Boolean.TRUE.equals(multiSegmentFlag)) {
                    multiSegmentParamNames.add(param.baseName);
                }
            }
        }
        // Add to operation vendor extensions for template access
        operation.vendorExtensions.put(X_MULTI_SEGMENT_PARAMS, multiSegmentParamNames);
    }
}
