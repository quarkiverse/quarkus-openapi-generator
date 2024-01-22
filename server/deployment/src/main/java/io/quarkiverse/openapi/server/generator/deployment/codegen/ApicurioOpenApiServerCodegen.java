package io.quarkiverse.openapi.server.generator.deployment.codegen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.server.generator.deployment.CodegenConfig;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

public class ApicurioOpenApiServerCodegen implements CodeGenProvider {

    private static final Logger log = LoggerFactory.getLogger(ApicurioCodegenWrapper.class);

    @Override
    public String providerId() {
        return "jaxrs";
    }

    @Override
    public String inputExtension() {
        return "json";
    }

    @Override
    public String inputDirectory() {
        return "resources";
    }

    @Override
    public boolean shouldRun(Path sourceDir, Config config) {
        return sourceDir != null && config.getOptionalValue(CodegenConfig.getSpecPropertyName(), String.class)
                .isPresent();
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        final Path openApiDir = context.inputDir();
        final Path outDir = context.outDir();
        final ApicurioCodegenWrapper apicurioCodegenWrapper = new ApicurioCodegenWrapper(context.config(), outDir.toFile());

        if (Files.isDirectory(openApiDir)) {

            try (Stream<Path> openApiFilesPaths = Files.walk(openApiDir)) {
                openApiFilesPaths
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .filter(s -> s.endsWith(this.inputExtension()))
                        .map(Path::of).forEach(openApiResource -> {
                            if (openApiResource.toFile().getName().equals(context.config()
                                    .getOptionalValue(CodegenConfig.getSpecPropertyName(), String.class).get())) {
                                try {
                                    apicurioCodegenWrapper.generate(openApiResource);
                                } catch (CodeGenException e) {
                                    log.warn("Exception found processing specification with name: {}",
                                            openApiResource.getFileName());
                                }
                            }
                        });

            } catch (IOException e) {
                throw new CodeGenException("Failed to generate java files from OpenApi file in " + openApiDir.toAbsolutePath(),
                        e);
            }
            return true;
        }
        return false;
    }
}
