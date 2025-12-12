package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.Config;
import org.jboss.logging.Logger;

import io.quarkiverse.openapi.server.generator.deployment.CodegenConfig;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

public class OpenAPIToolsServerCodegen implements CodeGenProvider {

    private static final String CODEGEN_TYPE = "openapitools";
    private static final Logger LOGGER = Logger.getLogger(OpenAPIToolsServerCodegen.class);
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".yaml", ".yml", ".json");
    private static final String OPENAPI_DIR = "openapi";

    @Override
    public String providerId() {
        return QuarkusJavaServerCodegen.CODEGEN_NAME;
    }

    @Override
    public String inputDirectory() {
        return "resources";
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {

        // must be configurable in the future
        Path inputDir = context.inputDir().resolve(OPENAPI_DIR);
        try (Stream<Path> paths = Files.walk(inputDir)) {

            List<Path> files = paths.filter(this::allowedFile).toList();

            for (Path file : files) {
                LOGGER.info("Generating server side code for: " + file.getFileName());

                QuarkusJavaServerCodegenConfigurator configurator = new QuarkusJavaServerCodegenConfigurator();

                // set input spec
                configurator.setInputSpec(file.toUri().toString());

                generate(configurator);
            }

        } catch (IOException e) {
            throw new CodeGenException("Unable to process files in " + inputDir.toAbsolutePath());
        }

        return true;
    }

    @Override
    public boolean shouldRun(Path sourceDir, Config config) {
        String serverCodegen = config.getOptionalValue(CodegenConfig.getServerUse(), String.class).orElse("apicurio");
        LOGGER.info("Generating server code using: [" + serverCodegen + "]");
        return serverCodegen.equalsIgnoreCase(CODEGEN_TYPE);
    }

    /**
     * Allowed files are regular files and must ends with '.json', '.yaml' or '.yml'.
     */
    private boolean allowedFile(Path path) {
        return Files.isRegularFile(path) && ALLOWED_EXTENSIONS.stream().anyMatch(ext -> path.toString().endsWith(ext));
    }

    private void generate(QuarkusJavaServerCodegenConfigurator configurator) {
        OpenAPIToolsGenerator generator = new OpenAPIToolsGenerator(configurator);
        List<File> generatedFiles = generator.generate();
        for (File generatedFile : generatedFiles) {
            LOGGER.info("Generated file: " + generatedFile);
        }
    }

}
