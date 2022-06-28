package io.quarkiverse.openapi.generator.deployment.wrapper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.utils.ProcessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;

import static org.openapitools.codegen.utils.OnceLogger.once;

public class QuarkusJavaClientCodegen extends JavaClientCodegen {

    private static final String AUTH_PACKAGE = "auth";
    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusJavaClientCodegen.class);
    public static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([^\\}]+)\\}");

    /*
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
                            "AuthenticationPropagationHeadersFactory.java"));

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
            once(LOGGER).warn("Server information seems not defined in the spec.");
            return Optional.empty();
        }
        // TODO need a way to obtain all server URLs
        return getServerURL(servers.get(0), userDefinedVariables);
    }

    private static Optional<URL> getServerURL(final Server server, final Map<String, String> userDefinedVariables) {
        String url = server.getUrl();
        ServerVariables variables = server.getVariables();
        if (variables == null) {
            variables = new ServerVariables();
        }

        Map<String, String> userVariables = userDefinedVariables == null ? new HashMap<>()
                : Collections.unmodifiableMap(userDefinedVariables);

        if (StringUtils.isNotBlank(url)) {
            url = extractUrl(server, url, variables, userVariables);
            url = sanitizeUrl(url);
            try {
                return Optional.of(new URL(url));
            } catch (MalformedURLException e) {
                once(LOGGER).warn("Not valid URL: {}", server.getUrl());
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private static String sanitizeUrl(String url) {
        if (url != null) {
            if (url.startsWith("//")) {
                url = "http:" + url;
                once(LOGGER).warn("'scheme' not defined in the spec (2.0). Default to [http] for server URL [{}]", url);
            } else if (url.startsWith("/")) {
                once(LOGGER).info(
                        "'host' (OAS 2.0) or 'servers' (OAS 3.0) not defined in the spec.");
            } else if (!url.matches("[a-zA-Z][0-9a-zA-Z.+\\-]+://.+")) {
                // Add http scheme for urls without a scheme.
                // 2.0 spec is restricted to the following schemes: "http", "https", "ws", "wss"
                // 3.0 spec does not have an enumerated list of schemes
                // This regex attempts to capture all schemes in IANA example schemes which
                // can have alpha-numeric characters and [.+-]. Examples are here:
                // https://www.iana.org/assignments/uri-schemes/uri-schemes.xhtml
                url = "http://" + url;
                once(LOGGER).warn("'scheme' not defined in the spec (2.0). Default to [http] for server URL [{}]", url);
            }
        }
        return url;
    }

    private static String extractUrl(Server server, String url, ServerVariables variables, Map<String, String> userVariables) {
        Set<String> replacedVariables = new HashSet<>();
        Matcher matcher = VARIABLE_PATTERN.matcher(url);
        while (matcher.find()) {
            if (!replacedVariables.contains(matcher.group())) {
                String variableName = matcher.group(1);
                ServerVariable variable = variables.get(variableName);
                String replacement;
                if (variable != null) {
                    String defaultValue = variable.getDefault();
                    List<String> enumValues = variable.getEnum() == null ? new ArrayList<>() : variable.getEnum();
                    if (defaultValue == null && !enumValues.isEmpty()) {
                        defaultValue = enumValues.get(0);
                    } else if (defaultValue == null) {
                        defaultValue = "";
                    }

                    replacement = userVariables.getOrDefault(variableName, defaultValue);

                    if (!enumValues.isEmpty() && !enumValues.contains(replacement)) {
                        once(LOGGER).warn("Variable override of '{}' is not listed in the enum of allowed values ({}).", replacement,
                                StringUtils.join(enumValues, ","));
                    }
                } else {
                    replacement = userVariables.getOrDefault(variableName, "");
                }

                if (StringUtils.isEmpty(replacement)) {
                    replacement = "";
                    once(LOGGER).warn(
                            "No value found for variable '{}' in server definition '{}' and no user override specified, default to empty string.",
                            variableName, server.getUrl());
                }

                url = url.replace(matcher.group(), replacement);
                replacedVariables.add(matcher.group());
                matcher = VARIABLE_PATTERN.matcher(url);
            }
        }
        return url;
    }

    @Override
    public void preprocessOpenAPI(OpenAPI openAPI) {
        super.preprocessOpenAPI(openAPI);
        // add the default server url to the context
        getServerURL(this.openAPI, serverVariableOverrides())
                .ifPresent(url -> additionalProperties.put(DEFAULT_SERVER_URL, url));
        additionalProperties.put(OpenApiClientGeneratorWrapper.DEFAULT_SECURITY_SCHEME,
                GlobalSettings.getProperty(OpenApiClientGeneratorWrapper.DEFAULT_SECURITY_SCHEME));
    }

    @Override
    public void postProcess() {
        final boolean verbose = Boolean.parseBoolean(GlobalSettings.getProperty(OpenApiClientGeneratorWrapper.VERBOSE));
        if (verbose) {
            super.postProcess();
        }
    }
}
