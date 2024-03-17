package io.quarkiverse.openapi.wiremock.generator.deployment.wrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.io.file.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.OpenApi2WiremockMapper;
import io.quarkiverse.openapi.wiremock.generator.deployment.wiremock.model.Stubbing;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.smallrye.config.common.utils.StringUtil;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class OpenApiWiremockGeneratorWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiWiremockGeneratorWrapper.class);
    public static final String MAPPINGS_DIR = "mappings";
    private static final String MAPPINGS = "mappings";
    private static final String WIREMOCK_OUTPUT_FILE = "wiremock-stubs.json";
    private static final OpenAPIV3Parser OPENAPI_PARSER_INSTANCE = new OpenAPIV3Parser();
    public static final ObjectMapper OBJECT_MAPPER_INSTANCE = new ObjectMapper();
    private final Path outputDir;
    private final Path inputDir;

    public OpenApiWiremockGeneratorWrapper(final Path inputDir, final Path outputDir) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
    }

    public void generate() throws CodeGenException, IOException {
        if (Files.isDirectory(inputDir) && !PathUtils.isEmpty(this.inputDir)) {
            try (Stream<Path> stream = Files.walk(this.inputDir)) {
                Path mappingsDir = createOutputMappingsDir(this.outputDir);
                stream.filter(Files::isRegularFile)
                        .forEach(generateWiremockStubbingFile(mappingsDir));
            } catch (IOException e) {
                throw new CodeGenException(
                        "Failed to generate Wiremock stubbing from OpenApi files in " + this.inputDir.toAbsolutePath(), e);
            }
        }
    }

    private Path createOutputMappingsDir(Path outDir) throws IOException {
        Path mappingsDir = resolveOutDir(outDir);
        if (!Files.exists(mappingsDir)) {
            Files.createDirectory(mappingsDir);
        }
        return mappingsDir;
    }

    private Consumer<Path> generateWiremockStubbingFile(Path mappingsDir) {
        return file -> {
            OpenAPI openAPI = OPENAPI_PARSER_INSTANCE.read(file.toString());
            OpenApi2WiremockMapper openApi2WiremockMapper = new OpenApi2WiremockMapper(openAPI);
            List<Stubbing> stubs = openApi2WiremockMapper.generateWiremockStubs();

            try {

                byte[] json = OBJECT_MAPPER_INSTANCE.writeValueAsBytes(Map.of(MAPPINGS, stubs));

                Path wiremockStubs = Files
                        .createFile(mappingsDir.resolve(generateFinalFilename(file)));

                Files.write(wiremockStubs, json);

            } catch (IOException e) {
                LOGGER.warn("Failed to generate Wiremock stubs for file " + file.toAbsolutePath(), e);
            }
        };
    }

    private String generateFinalFilename(Path path) {
        String filename = path.getFileName().toString();
        String sanitizedFilename = StringUtil.replaceNonAlphanumericByUnderscores(filename);
        return String.format("%s_%s", sanitizedFilename, WIREMOCK_OUTPUT_FILE);
    }

    private Path resolveOutDir(Path outDir) {
        return outDir.resolve(Path.of(MAPPINGS_DIR));
    }
}