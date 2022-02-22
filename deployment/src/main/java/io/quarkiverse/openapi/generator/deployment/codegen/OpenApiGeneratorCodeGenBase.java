package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.SpecConfig.getResolvedBasePackageProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.quarkiverse.openapi.generator.deployment.circuitbreaker.CircuitBreakerConfiguration;
import io.quarkiverse.openapi.generator.deployment.circuitbreaker.CircuitBreakerConfigurationParser;
import io.quarkiverse.openapi.generator.deployment.wrapper.OpenApiClientGeneratorWrapper;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

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
                            final CircuitBreakerConfiguration circuitBreakerConfiguration = getCircuitBreakerConfiguration(
                                    context,
                                    openApiFilePath);
                            
                            final OpenApiClientGeneratorWrapper generator = new OpenApiClientGeneratorWrapper(
                                    openApiFilePath.normalize(), outDir)
                                            .withCircuitBreakerConfiguration(circuitBreakerConfiguration);

                            context.config()
                                    .getOptionalValue(getResolvedBasePackageProperty(openApiFilePath), String.class)
                                    .ifPresent(generator::withBasePackage);
                            generator.generate();
                        });
            } catch (IOException e) {
                throw new CodeGenException("Failed to generate java files from OpenApi files in " + openApiDir.toAbsolutePath(),
                        e);
            }
            return true;
        }
        return false;
    }

    private CircuitBreakerConfiguration getCircuitBreakerConfiguration(CodeGenContext context, Path openApiFilePath) {
        UnaryOperator<String> nameToValuePropertyMapper = propertyName -> context.config().getValue(propertyName,
                String.class);

        return new CircuitBreakerConfigurationParser(openApiFilePath.toFile().getName(), nameToValuePropertyMapper)
                .parse(getConfigPropertyNames(context));
    }

    private static List<String> getConfigPropertyNames(CodeGenContext context) {
        return StreamSupport.stream(context.config().getPropertyNames().spliterator(), false)
                .collect(Collectors.toUnmodifiableList());
    }
}
