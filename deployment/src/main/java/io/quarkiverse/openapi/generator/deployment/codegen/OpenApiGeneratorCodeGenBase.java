package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.Config;
import org.openapitools.codegen.config.GlobalSettings;

import io.quarkiverse.openapi.generator.deployment.CodegenConfig;
import io.quarkiverse.openapi.generator.deployment.circuitbreaker.CircuitBreakerConfigurationParser;
import io.quarkiverse.openapi.generator.deployment.wrapper.OpenApiClientGeneratorWrapper;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;
import io.smallrye.config.SmallRyeConfig;

/**
 * Code generation for OpenApi Client. Generates Java classes from OpenApi spec files located in src/main/openapi or
 * src/test/openapi
 * <p>
 * Wraps the <a href="https://openapi-generator.tech/docs/generators/java">OpenAPI Generator Client for Java</a>
 */
public abstract class OpenApiGeneratorCodeGenBase implements CodeGenProvider {

    static final String YAML = ".yaml";
    static final String YML = ".yml";
    static final String JSON = ".json";

    private static final String DEFAULT_PACKAGE = "org.openapi.quarkus";

    @Override
    public String inputDirectory() {
        return "openapi";
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        final Path outDir = context.outDir();
        final Path openApiDir = context.inputDir();

        if (Files.isDirectory(openApiDir)) {
            try (Stream<Path> openApiFilesPaths = Files.walk(openApiDir)) {
                openApiFilesPaths
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .filter(s -> s.endsWith(this.inputExtension()))
                        .map(Path::of).forEach(openApiFilePath -> {
                            this.generate(context.config(), openApiFilePath, outDir);
                        });
            } catch (IOException e) {
                throw new CodeGenException("Failed to generate java files from OpenApi files in " + openApiDir.toAbsolutePath(),
                        e);
            }
            return true;
        }
        return false;
    }

    // TODO: do not generate if the output dir has generated files and the openapi file has the same checksum of the previous run
    protected void generate(final Config config, final Path openApiFilePath, final Path outDir) {
        final String basePackage = getBasePackage(config, openApiFilePath);
        final Boolean verbose = config.getOptionalValue(VERBOSE_PROPERTY_NAME, Boolean.class).orElse(false);
        final Boolean validateSpec = config.getOptionalValue(VALIDATE_SPEC_PROPERTY_NAME, Boolean.class).orElse(true);
        GlobalSettings.setProperty(OpenApiClientGeneratorWrapper.DEFAULT_SECURITY_SCHEME,
                config.getOptionalValue(CodegenConfig.DEFAULT_SECURITY_SCHEME, String.class).orElse(""));

        final OpenApiClientGeneratorWrapper generator = new OpenApiClientGeneratorWrapper(
                openApiFilePath.normalize(),
                outDir,
                verbose,
                validateSpec)
                .withClassesCodeGenConfig(ClassCodegenConfigParser.parse(config, basePackage))
                .withCircuitBreakerConfig(CircuitBreakerConfigurationParser.parse(
                        config));

        config.getOptionalValue(getSkipFormModelPropertyName(openApiFilePath), String.class)
                .ifPresent(generator::withSkipFormModelConfig);

        config.getOptionalValue(getAdditionalModelTypeAnnotationsPropertyName(openApiFilePath), String.class)
                .ifPresent(generator::withAdditionalModelTypeAnnotationsConfig);

        config.getOptionalValue(getCustomRegisterProvidersFormat(openApiFilePath), String.class)
                .ifPresent(generator::withCustomRegisterProviders);

        SmallRyeConfig smallRyeConfig = config.unwrap(SmallRyeConfig.class);
        smallRyeConfig.getOptionalValues(getTypeMappingsPropertyName(openApiFilePath), String.class, String.class)
                .ifPresent(generator::withTypeMappings);

        smallRyeConfig.getOptionalValues(getImportMappingsPropertyName(openApiFilePath), String.class, String.class)
                .ifPresent(generator::withImportMappings);

        generator.generate(basePackage);
    }

    private String getBasePackage(final Config config, final Path openApiFilePath) {
        return config
                .getOptionalValue(getBasePackagePropertyName(openApiFilePath), String.class)
                .orElse(String.format("%s.%s", DEFAULT_PACKAGE, getSanitizedFileName(openApiFilePath)));
    }
}
