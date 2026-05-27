package io.quarkiverse.openapi.server.generator.deployment.codegen.apicurio;

import static io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig.DEFAULT_DIR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.quarkiverse.openapi.server.generator.deployment.CodegenConfig;
import io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

public class ApicurioOpenApiServerCodegen implements CodeGenProvider {

    private static final Logger log = LoggerFactory.getLogger(ApicurioOpenApiServerCodegen.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

    private Optional<String> getInputBaseDirRelativeToModule(final Path sourceDir, final Config config) {
        return config.getOptionalValue(CodegenConfig.getInputBaseDirPropertyName(), String.class)
                .or(() -> config.getOptionalValue(CodegenConfig.getServerInputBaseDirPropertyName(), String.class))
                .map(baseDir -> {
                    int srcIndex = sourceDir.toString().lastIndexOf("src");
                    return srcIndex < 0 ? null : Path.of(sourceDir.toString().substring(0, srcIndex), baseDir).toString();
                });
    }

    @Override
    public boolean shouldRun(Path sourceDir, Config config) {

        if (sourceDir.endsWith(Path.of("src", "test", this.inputDirectory()))) {
            // skip when generate-code-tests
            return false;
        }

        String serverCodegen = config.getOptionalValue(CodegenConfig.getServerUse(), String.class)
                .orElse(ServerCodegenConfig.APICURIO);
        if (!serverCodegen.equalsIgnoreCase(ServerCodegenConfig.APICURIO)) {
            return false;
        }
        log.info("Generating server code using: [{}]", serverCodegen);

        String specPropertyName = config.getOptionalValue(CodegenConfig.getSpecPropertyName(), String.class)
                .or(() -> config.getOptionalValue(CodegenConfig.getServerSpecPropertyName(), String.class))
                .orElse(null);

        if (specPropertyName == null) {
            log.warn("The {} property is not present, the code generation will be ignored",
                    CodegenConfig.getSpecPropertyName());
            return false;
        }

        String relativeInputBaseDir = getInputBaseDirRelativeToModule(sourceDir, config).orElse(null);
        if (relativeInputBaseDir != null) {
            return Files.exists(Path.of(relativeInputBaseDir).resolve(specPropertyName));
        } else {
            return Files.exists(sourceDir.resolve(DEFAULT_DIR).resolve(specPropertyName));
        }
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        Config config = context.config();
        final Path openApiDir = Path.of(getInputBaseDirRelativeToModule(context.inputDir(), config)
                .orElse(context.inputDir().resolve(DEFAULT_DIR).toString()));

        validateOpenApiDir(context, openApiDir);

        final Path outDir = context.outDir();
        final ApicurioCodegenWrapper apicurioCodegenWrapper = new ApicurioCodegenWrapper(
                config, outDir.toFile());
        final String specPropertyName = config
                .getOptionalValue(CodegenConfig.getSpecPropertyName(), String.class)
                .or(() -> config.getOptionalValue(CodegenConfig.getServerSpecPropertyName(), String.class))
                .orElseThrow();

        final File openApiResource = new File(openApiDir.toFile(), specPropertyName);
        if (!openApiResource.exists()) {
            throw new CodeGenException(
                    "Specification file not found: " + openApiResource.getAbsolutePath());
        }
        if (!openApiResource.isFile()) {
            throw new CodeGenException(
                    "Specification file is not a file: " + openApiResource.getAbsolutePath());
        }
        if (!openApiResource.canRead()) {
            throw new CodeGenException(
                    "Specification file is not readable: " + openApiResource.getAbsolutePath());
        }
        if (Arrays.stream(this.inputExtensions()).noneMatch(specPropertyName::endsWith)) {
            throw new CodeGenException(
                    "Specification file must have one of the following extensions: " + Arrays.toString(
                            this.inputExtensions()));
        }

        // Apicurio only supports JSON => convert YAML to JSON
        final File jsonSpec = specPropertyName.endsWith("json") ? openApiResource
                : convertToJSON(openApiResource.toPath());

        // Copy original (JSON) spec to output directory
        final String originalSpecName = specPropertyName.replaceAll("\\.(yaml|yml)$", ".json");
        try {
            Files.copy(jsonSpec.toPath(), outDir.resolve(originalSpecName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new CodeGenException("Error copying original spec to output directory", e);
        }

        final Map<String, String> returnTypes = collectReturnTypes(config);
        final Path specToUse;
        if (returnTypes.isEmpty()) {
            specToUse = jsonSpec.toPath();
        } else {
            final String modifiedSpecName = originalSpecName.replace(".json", "-modified.json");
            specToUse = injectReturnTypes(jsonSpec.toPath(), returnTypes, outDir.resolve(modifiedSpecName));
        }

        apicurioCodegenWrapper.generate(specToUse);
        return true;
    }

    private static void validateOpenApiDir(CodeGenContext context, Path openApiDir) throws CodeGenException {
        if (!Files.exists(openApiDir)) {
            throw new CodeGenException(
                    "The OpenAPI input base directory does not exist. Please create the directory at " + context.inputDir());
        }

        if (!Files.isDirectory(openApiDir)) {
            throw new CodeGenException(
                    "The OpenAPI input base directory is not a directory. Please create the directory at "
                            + context.inputDir());
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

    private File convertToJSON(Path yamlPath) throws CodeGenException {
        try {
            ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
            Object obj = yamlReader.readValue(yamlPath.toFile(), Object.class);
            ObjectMapper jsonWriter = new ObjectMapper();
            File jsonFile = File.createTempFile(yamlPath.toFile().getName(), ".json");
            jsonFile.deleteOnExit();
            jsonWriter.writeValue(jsonFile, obj);
            return jsonFile;
        } catch (Exception e) {
            throw new CodeGenException("Error converting YAML to JSON", e);
        }
    }
}
