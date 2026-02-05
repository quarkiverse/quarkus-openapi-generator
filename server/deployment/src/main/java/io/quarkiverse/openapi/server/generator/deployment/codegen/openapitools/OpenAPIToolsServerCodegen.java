package io.quarkiverse.openapi.server.generator.deployment.codegen.openapitools;

import static io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig.APICURIO;
import static io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig.DEFAULT_DIR;
import static io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig.DEFAULT_PACKAGE;
import static io.quarkiverse.openapi.server.generator.deployment.ServerCodegenConfig.OPENAPITOOLS;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.jboss.logging.Logger;

import io.quarkiverse.openapi.server.generator.deployment.CodegenConfig;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

public class OpenAPIToolsServerCodegen implements CodeGenProvider {

    private static final Logger LOGGER = Logger.getLogger(OpenAPIToolsServerCodegen.class);

    @Override
    public String providerId() {
        return QuarkusJavaServerCodegen.CODEGEN_NAME;
    }

    @Override
    public String inputDirectory() {
        return "resources";
    }

    @Override
    public boolean trigger(CodeGenContext context) {

        final Path inputDir = Path.of(getInputBaseDirRelativeToModule(context.inputDir(), context.config())
                .orElse(context.inputDir().resolve(DEFAULT_DIR).toString()));

        Path outputDir = context.outDir();
        String specPropertyName = getSpecPropertyName(context);
        File openAPIFile = new File(inputDir.toFile(), specPropertyName);

        LOGGER.info("Generating server side code for: " + openAPIFile);

        QuarkusJavaServerCodegenConfigurator configurator = new QuarkusJavaServerCodegenConfigurator()
                .withBasePackage(basePackage(context))
                .withGenerateBuilders(generateBuilders(context))
                .withBeanValidation(beanValidation(context))
                .withReactive(reactive(context))
                .withInputBaseDir(openAPIFile.toString())
                .withOutputDir(
                        outputDir.toAbsolutePath().toString());

        generate(configurator);

        return true;
    }

    @Override
    public boolean shouldRun(Path sourceDir, Config config) {
        String serverCodegen = config.getOptionalValue(CodegenConfig.getServerUse(), String.class)
                .orElse(APICURIO);
        return serverCodegen.equalsIgnoreCase(OPENAPITOOLS);
    }

    private void generate(QuarkusJavaServerCodegenConfigurator configurator) {
        OpenAPIToolsGenerator generator = new OpenAPIToolsGenerator(configurator);
        List<File> generatedFiles = generator.generate();
        for (File generatedFile : generatedFiles) {
            LOGGER.info("Generated file: " + generatedFile);
        }
    }

    private Optional<String> getInputBaseDirRelativeToModule(final Path sourceDir, final Config config) {
        return config.getOptionalValue(CodegenConfig.getInputBaseDirPropertyName(), String.class).map(baseDir -> {
            int srcIndex = sourceDir.toString().lastIndexOf("src");
            return srcIndex < 0 ? null : Path.of(sourceDir.toString().substring(0, srcIndex), baseDir).toString();
        });
    }

    private boolean beanValidation(CodeGenContext context) {
        Boolean beanValidation = context.config().getOptionalValue(CodegenConfig.getServerUseBeanValidation(), Boolean.class)
                .orElse(false);

        boolean hibernateValidatorCapabilityIsPresent = context.applicationModel().getExtensionCapabilities().stream()
                .flatMap(extensionCapability -> extensionCapability.getProvidesCapabilities().stream())
                .anyMatch(Capability.HIBERNATE_VALIDATOR::equals);

        if (!hibernateValidatorCapabilityIsPresent) {
            throw new IllegalStateException(
                    "The extension io.quarkus:quarkus-hibernate-validator is required when the property " +
                            "quarkus.openapi.generator.server.bean-validation is set to true.");
        }

        return beanValidation;
    }

    private boolean generateBuilders(CodeGenContext context) {
        return context.config().getOptionalValue(CodegenConfig.getServerGenerateBuilders(), Boolean.class)
                .orElse(true);
    }

    private String getSpecPropertyName(CodeGenContext context) {
        return context.config()
                .getOptionalValue(CodegenConfig.getSpecPropertyName(), String.class)
                .orElse(context.config().getOptionalValue(CodegenConfig.getServerSpecPropertyName(), String.class).orElseThrow(
                        () -> new IllegalStateException(
                                "You need to provide the OpenAPI file through 'quarkus.openapi.generator.server.spec' property.")));
    }

    private String basePackage(CodeGenContext context) {
        return context.config().getOptionalValue(CodegenConfig.getServerBasePackagePropertyName(), String.class)
                .orElse(DEFAULT_PACKAGE);
    }

    private boolean reactive(CodeGenContext context) {
        return context.config().getOptionalValue(CodegenConfig.getServerCodegenReactive(), Boolean.class)
                .orElse(false);
    }

}
