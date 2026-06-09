package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import static io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig.APICURIO;
import static io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig.OPENAPITOOLS;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.microprofile.config.Config;
import org.jboss.logging.Logger;

import io.quarkiverse.openapi.generator.common.OpenApiGeneratorOptions;
import io.quarkiverse.openapi.generator.common.SkipGenerationSupport;
import io.quarkiverse.openapi.server.generator.deployment.CodegenConfig;
import io.quarkiverse.openapi.server.generator.deployment.codegen.ServerCodegenConfigResolver;
import io.quarkiverse.openapi.server.generator.deployment.codegen.ServerCodegenSpec;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;
import io.smallrye.config.common.utils.StringUtil;

public class OpenAPIToolsServerCodegen implements CodeGenProvider {

    private static final Logger LOGGER = Logger.getLogger(OpenAPIToolsServerCodegen.class);
    private final ServerCodegenConfigResolver configResolver = new ServerCodegenConfigResolver();

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
        Path outputDir = context.outDir();
        for (ServerCodegenSpec spec : configResolver.resolveSpecs(context.inputDir(), context.config())) {
            File openAPIFile = spec.specPath().toFile();

            LOGGER.info("Generating server side code for: " + openAPIFile);

            QuarkusJavaServerCodegenConfigurator configurator = new QuarkusJavaServerCodegenConfigurator()
                    .withBasePackage(spec.basePackage())
                    .withGenerateBuilders(spec.builders())
                    .withBeanValidation(beanValidation(context, spec))
                    .withReactive(spec.reactive())
                    .withRestResponse(restResponse(context))
                    .withInputBaseDir(openAPIFile.toString())
                    .withOutputDir(outputDir.toAbsolutePath().toString());

            OpenApiGeneratorOptions options = new OpenApiGeneratorOptions(
                    getClass(),
                    CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX,
                    context.config(),
                    openAPIFile.toPath(),
                    StringUtil.replaceNonAlphanumericByUnderscores(openAPIFile.getName()),
                    outputDir.toAbsolutePath(),
                    null,
                    spec.reactive());

            generate(configurator, options);
        }

        return true;
    }

    @Override
    public boolean shouldRun(Path sourceDir, Config config) {

        if (sourceDir.endsWith(Path.of("src", "test", this.inputDirectory()))) {
            // skip when generate-code-tests
            return false;
        }

        String serverCodegen = config.getOptionalValue(CodegenConfig.getServerUse(), String.class)
                .orElse(APICURIO);
        return serverCodegen.equalsIgnoreCase(OPENAPITOOLS) && configResolver.hasConfiguration(sourceDir, config);
    }

    protected void generate(QuarkusJavaServerCodegenConfigurator configurator, OpenApiGeneratorOptions options) {
        boolean skipIfUnchanged = options.config()
                .getOptionalValue(CodegenConfig.getServerSkipIfUnchanged(), Boolean.class)
                .orElse(false);

        if (!skipIfUnchanged) {
            doGenerate(new OpenAPIToolsGenerator(configurator));
            return;
        }

        var skipGenerationSupport = new SkipGenerationSupport();
        String fingerprint = skipGenerationSupport.computeFingerprint(options);
        if (skipGenerationSupport.shouldSkipGeneration(options, fingerprint)) {
            LOGGER.info(
                    "Skipping code generation as the OpenAPI spec file and configuration haven't changed since the last generation.");
            return;
        }

        doGenerate(new OpenAPIToolsGenerator(configurator));
        skipGenerationSupport.persistFingerprint(options, fingerprint);
    }

    protected void doGenerate(OpenAPIToolsGenerator generator) {
        List<File> generatedFiles = generator.generate();
        for (File generatedFile : generatedFiles) {
            LOGGER.info("Generated file: " + generatedFile);
        }
    }

    private boolean beanValidation(CodeGenContext context, ServerCodegenSpec spec) {
        if (!spec.beanValidation()) {
            return false;
        }

        boolean hibernateValidatorCapabilityIsPresent = context.applicationModel() != null
                && context.applicationModel().getExtensionCapabilities().stream()
                        .flatMap(extensionCapability -> extensionCapability.getProvidesCapabilities().stream())
                        .anyMatch(Capability.HIBERNATE_VALIDATOR::equals);

        if (!hibernateValidatorCapabilityIsPresent) {
            throw new IllegalStateException(
                    "The extension io.quarkus:quarkus-hibernate-validator is required when the property "
                            + "quarkus.openapi.generator.server.bean-validation is set to true.");
        }

        return true;
    }

    private boolean restResponse(CodeGenContext context) {
        return context.config().getOptionalValue(CodegenConfig.getServerUseRestResponse(), Boolean.class)
                .orElse(false);
    }

}
