package io.quarkiverse.openapi.generator.deployment.codegen;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.DEFAULT_SECURITY_SCHEME;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.VALIDATE_SPEC_PROPERTY_NAME;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.VERBOSE_PROPERTY_NAME;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getAdditionalModelTypeAnnotationsPropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getCustomRegisterProvidersFormat;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getImportMappingsPropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getSkipFormModelPropertyName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getTypeMappingsPropertyName;

import java.nio.file.Path;

import org.eclipse.microprofile.config.Config;
import org.openapitools.codegen.config.GlobalSettings;

import io.quarkiverse.openapi.generator.deployment.CodegenConfig;
import io.quarkiverse.openapi.generator.deployment.circuitbreaker.CircuitBreakerConfigurationParser;
import io.quarkiverse.openapi.generator.deployment.wrapper.OpenApiClientGeneratorWrapper;
import io.quarkiverse.spec.generator.deployment.codegen.SpecApiCodeGenUtils;
import io.quarkiverse.spec.generator.deployment.codegen.SpecCodeGenerator;
import io.smallrye.config.SmallRyeConfig;

public class OpenApiGeneratorCodeGenerator implements SpecCodeGenerator {

    @Override
    public void generate(Config config, Path openApiFilePath, Path outDir) {

        final String basePackage = config
                .getOptionalValue(CodegenConfig.getBasePackagePropertyName(openApiFilePath), String.class)
                .orElse("org.openapi.quarkus." + SpecApiCodeGenUtils.getSanitizedFileName(openApiFilePath));
        final Boolean verbose = config.getOptionalValue(VERBOSE_PROPERTY_NAME, Boolean.class).orElse(false);
        final Boolean validateSpec = config.getOptionalValue(VALIDATE_SPEC_PROPERTY_NAME, Boolean.class).orElse(true);
        GlobalSettings.setProperty(OpenApiClientGeneratorWrapper.DEFAULT_SECURITY_SCHEME,
                config.getOptionalValue(DEFAULT_SECURITY_SCHEME, String.class).orElse(""));

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
}
