package io.quarkiverse.spec.generator.deployment.codegen;

import static io.quarkiverse.spec.generator.deployment.codegen.SpecApiCodeGenUtils.getBasePackagePropertyName;
import static io.quarkiverse.spec.generator.deployment.codegen.SpecApiCodeGenUtils.getSanitizedFileName;

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
public abstract class SpecApiGeneratorCodeGenBase implements CodeGenProvider {

    protected final SpecCodeGenerator generator;
    private final SpecApiConstants constants;

    protected SpecApiGeneratorCodeGenBase(SpecCodeGenerator generator, SpecApiConstants constants) {
        this.generator = generator;
        this.constants = constants;
    }

    @Override
    public String inputDirectory() {
        return constants.getInputDirectory();
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
        return constants.getProviderPrefix() + "-" + constants.getExtension();
    }

    @Override
    public String inputExtension() {
        return "." + constants.getExtension();
    }

    protected final String getBasePackage(final Config config, final Path openApiFilePath) {
        return config
                .getOptionalValue(getBasePackagePropertyName(openApiFilePath, constants.getConfigPrefix()), String.class)
                .orElse(String.format("%s.%s", constants.getDefaultPackage(), getSanitizedFileName(openApiFilePath)));
    }
}
