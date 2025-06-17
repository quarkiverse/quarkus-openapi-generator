package io.quarkiverse.openapi.server.generator.deployment.codegen;

import static io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig.DEFAULT_DIR;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.quarkiverse.openapi.server.generator.deployment.CodegenConfig;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

public class ApicurioOpenApiServerCodegen implements CodeGenProvider {

    private static final Logger log = LoggerFactory.getLogger(ApicurioOpenApiServerCodegen.class);

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
        return config.getOptionalValue(CodegenConfig.getInputBaseDirPropertyName(), String.class).map(baseDir -> {
            int srcIndex = sourceDir.toString().lastIndexOf("src");
            return srcIndex < 0 ? null : Path.of(sourceDir.toString().substring(0, srcIndex), baseDir).toString();
        });
    }

    @Override
    public boolean shouldRun(Path sourceDir, Config config) {
        Optional<String> possibleSpecPropertyName = config.getOptionalValue(CodegenConfig.getSpecPropertyName(), String.class);
        if (possibleSpecPropertyName.isEmpty()) {
            log.warn("The {} property is not present, the code generation will be ignored",
                    CodegenConfig.getSpecPropertyName());
            return false;
        }
        String specPropertyName = possibleSpecPropertyName.get();
        String relativeInputBaseDir = getInputBaseDirRelativeToModule(sourceDir, config).orElse(null);
        if (relativeInputBaseDir != null) {
            return Files.exists(Path.of(relativeInputBaseDir).resolve(specPropertyName));
        } else {
            return Files.exists(sourceDir.resolve(DEFAULT_DIR).resolve(specPropertyName));
        }
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        final Path openApiDir = Path.of(getInputBaseDirRelativeToModule(context.inputDir(), context.config())
                .orElse(context.inputDir().resolve(DEFAULT_DIR).toString()));

        validateOpenApiDir(context, openApiDir);

        final Path outDir = context.outDir();
        final ApicurioCodegenWrapper apicurioCodegenWrapper = new ApicurioCodegenWrapper(
                context.config(), outDir.toFile());
        final String specPropertyName = context.config()
                .getOptionalValue(CodegenConfig.getSpecPropertyName(), String.class)
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
        // Apicurio only supports JSON => convert yaml to JSON
        final File jsonSpec = specPropertyName.endsWith("json") ? openApiResource
                : convertToJSON(openApiResource.toPath());
        try {
            apicurioCodegenWrapper.generate(jsonSpec.toPath());
        } catch (CodeGenException e) {
            log.warn("Exception found processing specification with name: {}",
                    openApiResource.getAbsolutePath());
        }
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
