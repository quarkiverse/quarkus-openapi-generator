package io.quarkiverse.openapi.server.generator.deployment.codegen.apicurio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkiverse.openapi.server.generator.deployment.CodegenConfig;
import io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig;
import io.quarkiverse.openapi.server.generator.deployment.codegen.ServerCodegenConfigResolver;
import io.quarkiverse.openapi.server.generator.deployment.codegen.ServerCodegenSpec;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class ApicurioOpenApiServerCodegen implements CodeGenProvider {

    private static final Logger log = LoggerFactory.getLogger(ApicurioOpenApiServerCodegen.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ServerCodegenConfigResolver configResolver = new ServerCodegenConfigResolver();

    @Override
    public String providerId() {
        return "jaxrs";
    }

    @Override
    public String[] inputExtensions() {
        return new String[] { "json", "yaml", "yml" };
    }

    @Override
    public String inputDirectory() {
        return "resources";
    }

    @Override
    public boolean shouldRun(Path sourceDir, Config config) {

        // Only run for the main source directory to prevent multiple executions
        // when the project has multiple source roots
        if (!sourceDir.endsWith(Path.of("src", "main", this.inputDirectory()))) {
            return false;
        }

        String serverCodegen = config.getOptionalValue(CodegenConfig.getServerUse(), String.class)
                .orElse(ServerCodegenConfig.APICURIO);
        if (!serverCodegen.equalsIgnoreCase(ServerCodegenConfig.APICURIO)) {
            return false;
        }
        log.info("Generating server code using: [{}]", serverCodegen);

        return configResolver.hasConfiguration(sourceDir, config);
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        Config config = context.config();
        Path outDir = context.outDir();
        List<ServerCodegenSpec> specs = configResolver.resolveSpecs(context.inputDir(), config);
        for (ServerCodegenSpec spec : specs) {
            File openApiResource = spec.specPath().toFile();
            validateOpenApiResource(openApiResource);

            String specFileName = openApiResource.getName();
            if (Arrays.stream(this.inputExtensions()).noneMatch(specFileName::endsWith)) {
                throw new CodeGenException(
                        "Specification file must have one of the following extensions: " + Arrays.toString(
                                this.inputExtensions()));
            }

            File jsonSpec = specFileName.endsWith("json") ? openApiResource : resolveToJSON(openApiResource.toPath());

            String originalSpecName = specFileName.replaceAll("\\.(yaml|yml)$", ".json");
            try {
                Path originalSpecPath = outDir.resolve(originalSpecName);
                Files.createDirectories(originalSpecPath.getParent());
                Files.copy(jsonSpec.toPath(), originalSpecPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new CodeGenException("Error copying original spec to output directory", e);
            }

            Map<String, String> returnTypes = collectReturnTypes(config);
            final Path specToUse;
            if (returnTypes.isEmpty()) {
                specToUse = jsonSpec.toPath();
            } else {
                String modifiedSpecName = originalSpecName.replace(".json", "-modified.json");
                specToUse = injectReturnTypes(jsonSpec.toPath(), returnTypes, outDir.resolve(modifiedSpecName));
            }

            new ApicurioCodegenWrapper(outDir.toFile(), spec).generate(specToUse);
        }
        return true;
    }

    private static void validateOpenApiResource(File openApiResource) throws CodeGenException {
        if (!openApiResource.exists()) {
            throw new CodeGenException("Specification file not found: " + openApiResource.getAbsolutePath());
        }
        if (!openApiResource.isFile()) {
            throw new CodeGenException("Specification file is not a file: " + openApiResource.getAbsolutePath());
        }
        if (!openApiResource.canRead()) {
            throw new CodeGenException("Specification file is not readable: " + openApiResource.getAbsolutePath());
        }
    }

    private Map<String, String> collectReturnTypes(Config config) {
        final String prefix = CodegenConfig.getServerOperationIds() + ".";
        final String suffix = ".return-type";
        Map<String, String> returnTypes = new HashMap<>();
        for (String propertyName : config.getPropertyNames()) {
            if (propertyName.startsWith(prefix) && propertyName.endsWith(suffix)) {
                String operationId = propertyName.substring(prefix.length(),
                        propertyName.length() - suffix.length());
                config.getOptionalValue(propertyName, String.class)
                        .ifPresent(v -> returnTypes.put(operationId, v));
            }
        }
        return returnTypes;
    }

    private Path injectReturnTypes(Path jsonSpecPath, Map<String, String> returnTypes, Path outputPath)
            throws CodeGenException {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(jsonSpecPath.toFile());
            JsonNode paths = root.path("paths");
            if (!paths.isMissingNode()) {
                paths.fields().forEachRemaining(pathEntry -> {
                    pathEntry.getValue().fields().forEachRemaining(methodEntry -> {
                        JsonNode operation = methodEntry.getValue();
                        JsonNode operationIdNode = operation.path("operationId");
                        if (!operationIdNode.isMissingNode()) {
                            String operationId = operationIdNode.asText();
                            String returnType = returnTypes.get(operationId);
                            if (returnType != null) {
                                JsonNode responses = operation.path("responses");
                                responses.fields().forEachRemaining(responseEntry -> {
                                    JsonNode content = responseEntry.getValue().path("content");
                                    content.fields().forEachRemaining(mediaTypeEntry -> {
                                        JsonNode mediaTypeNode = mediaTypeEntry.getValue();
                                        if (mediaTypeNode.isObject()) {
                                            ((ObjectNode) mediaTypeNode).put("x-codegen-returnType", returnType);
                                        }
                                    });
                                });
                            }
                        }
                    });
                });
            }
            OBJECT_MAPPER.writeValue(outputPath.toFile(), root);
            return outputPath;
        } catch (IOException e) {
            throw new CodeGenException("Error injecting x-codegen-returnType into spec", e);
        }
    }

    private File resolveToJSON(Path specPath) throws CodeGenException {
        try {
            SwaggerParseResult parseResult = parseAndResolve(specPath);
            OpenAPI openAPI = parseResult.getOpenAPI();
            if (openAPI == null) {
                throw new CodeGenException("Error parsing OpenAPI spec: " + parseMessages(parseResult));
            }

            File jsonFile = Files.createTempFile(specPath.getFileName().toString(), ".json").toFile();
            jsonFile.deleteOnExit();
            Json.mapper().writeValue(jsonFile, openAPI);
            return jsonFile;
        } catch (Exception e) {
            throw new CodeGenException("Error resolving OpenAPI spec to JSON", e);
        }
    }

    private SwaggerParseResult parseAndResolve(Path specPath) throws CodeGenException {
        ParseOptions options = new ParseOptions();
        options.setResolve(true);
        options.setResolveFully(true);
        options.setResolveCombinators(true);

        SwaggerParseResult parseResult = new OpenAPIV3Parser()
                .readLocation(specPath.toUri().toString(), null, options);

        if (parseResult == null) {
            throw new CodeGenException("OpenAPI parser returned no result for: " + specPath);
        }
        if (parseResult.getMessages() != null && !parseResult.getMessages().isEmpty()) {
            log.warn("OpenAPI parse warnings for {}: {}", specPath, String.join("; ", parseResult.getMessages()));
        }
        return parseResult;
    }

    private static String parseMessages(SwaggerParseResult parseResult) {
        List<String> messages = parseResult.getMessages();
        if (messages == null || messages.isEmpty()) {
            return "Unknown parsing error";
        }
        return String.join("; ", messages);
    }
}
