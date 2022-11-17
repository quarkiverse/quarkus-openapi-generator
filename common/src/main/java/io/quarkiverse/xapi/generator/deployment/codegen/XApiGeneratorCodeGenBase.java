package io.quarkiverse.xapi.generator.deployment.codegen;

import static io.quarkiverse.xapi.generator.deployment.codegen.CodegenConfig.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.Config;

import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

/**
 * Code generation for OpenApi Client. Generates Java classes from OpenApi spec files located in src/main/openapi or
 * src/test/openapi
 * <p>
 * Wraps the <a href="https://openapi-generator.tech/docs/generators/java">OpenAPI Generator Client for Java</a>
 */
public abstract class XApiGeneratorCodeGenBase implements CodeGenProvider {

    protected static final String YAML = ".yaml";
    protected static final String YML = ".yml";
    protected static final String JSON = ".json";

    protected final CodeGenerator generator;

    protected XApiGeneratorCodeGenBase(CodeGenerator generator) {
        this.generator = generator;
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        final Path outDir = context.outDir();
        final Path openApiDir = context.inputDir();
        final List<String> ignoredFiles = context.config()
                .getOptionalValues("quarkus.openapi-generator.codegen.ignore", String.class).orElse(List.of());

        if (Files.isDirectory(openApiDir)) {
            try (Stream<Path> openApiFilesPaths = Files.walk(openApiDir)) {
                openApiFilesPaths
                        .filter(Files::isRegularFile)
                        .filter(path -> {
                            String fileName = path.getFileName().toString();
                            return fileName.endsWith(inputExtension()) && !ignoredFiles.contains(fileName);
                        })
                        .forEach(openApiFilePath -> generator.generate(context.config(), openApiFilePath, outDir,
                                getBasePackage(context.config(), openApiFilePath)));
            } catch (IOException e) {
                throw new CodeGenException("Failed to generate java files from OpenApi files in " + openApiDir.toAbsolutePath(),
                        e);
            }
            return true;
        }
        return false;
    }

    @Override
    public String providerId() {
        return providerPrefix() + "-" + inputExtension().substring(1);
    }

    protected abstract String getDefaultPackage();

    protected String providerPrefix() {
        return inputDirectory();
    }

    protected final String getBasePackage(final Config config, final Path openApiFilePath) {
        return config
                .getOptionalValue(getBasePackagePropertyName(openApiFilePath), String.class)
                .orElse(String.format("%s.%s", getDefaultPackage(), getSanitizedFileName(openApiFilePath)));
    }
}
