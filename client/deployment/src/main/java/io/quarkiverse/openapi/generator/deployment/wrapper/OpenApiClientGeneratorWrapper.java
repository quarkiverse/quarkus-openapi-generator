package io.quarkiverse.openapi.generator.deployment.wrapper;

import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.getSanitizedFileName;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.resolveApiPackage;
import static io.quarkiverse.openapi.generator.deployment.CodegenConfig.resolveModelPackage;
import static io.quarkiverse.openapi.generator.deployment.wrapper.QuarkusJavaClientCodegen.QUARKUS_GENERATOR_NAME;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.requireNonNull;
import static org.openapitools.codegen.languages.AbstractJavaCodegen.ADDITIONAL_MODEL_TYPE_ANNOTATIONS;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.GlobalSettings;

import io.smallrye.config.common.utils.StringUtil;

/**
 * Wrapper for the OpenAPIGen tool.
 * This is the same as calling the Maven plugin or the CLI.
 * We are wrapping into a class to generate code that meet our requirements.
 *
 * @see <a href="https://openapi-generator.tech/docs/generators/java">OpenAPI Generator Client for Java</a>
 */
public abstract class OpenApiClientGeneratorWrapper {

    public static final String VERBOSE = "verbose";
    private static final String ONCE_LOGGER = "org.openapitools.codegen.utils.oncelogger.enabled";
    /**
     * Security scheme for which to apply security constraints even if the OpenAPI definition has no security definition
     */
    public static final String DEFAULT_SECURITY_SCHEME = "defaultSecurityScheme";
    public static final String SUPPORTS_ADDITIONAL_PROPERTIES_AS_ATTRIBUTE = "supportsAdditionalPropertiesWithComposedSchema";
    private static final Map<String, String> defaultTypeMappings = Map.of(
            "date", "LocalDate",
            "DateTime", "OffsetDateTime");
    private static final Map<String, String> defaultImportMappings = Map.of(
            "LocalDate", "java.time.LocalDate",
            "OffsetDateTime", "java.time.OffsetDateTime");
    private final QuarkusCodegenConfigurator configurator;
    private final DefaultGenerator generator;

    private String basePackage = "";
    private String apiPackage = "";
    private String modelPackage = "";

    OpenApiClientGeneratorWrapper(final QuarkusCodegenConfigurator configurator, final Path specFilePath, final Path outputDir,
            final boolean verbose,
            final boolean validateSpec) {
        // do not generate docs nor tests
        GlobalSettings.setProperty(CodegenConstants.API_DOCS, FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.API_TESTS, FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.MODEL_TESTS, FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.MODEL_DOCS, FALSE.toString());
        // generates every Api and Models
        GlobalSettings.setProperty(CodegenConstants.APIS, "");
        GlobalSettings.setProperty(CodegenConstants.MODELS, "");
        GlobalSettings.setProperty(CodegenConstants.SUPPORTING_FILES, "");
        // logging
        GlobalSettings.setProperty(VERBOSE, String.valueOf(verbose));
        GlobalSettings.setProperty(ONCE_LOGGER, verbose ? FALSE.toString() : TRUE.toString());

        this.configurator = configurator;
        this.configurator.setInputSpec(specFilePath.toString());
        this.configurator.setOutputDir(outputDir.toString());
        this.configurator.addAdditionalProperty(QUARKUS_GENERATOR_NAME,
                Collections.singletonMap("openApiSpecId", getSanitizedFileName(specFilePath)));
        this.configurator.addAdditionalProperty("openApiNullable", false);
        this.configurator.setValidateSpec(validateSpec);
        defaultTypeMappings.forEach(this.configurator::addTypeMapping);
        defaultImportMappings.forEach(this.configurator::addImportMapping);

        this.generator = new DefaultGenerator();
    }

    public OpenApiClientGeneratorWrapper withApiPackage(final String pkg) {
        this.apiPackage = pkg;
        return this;
    }

    public OpenApiClientGeneratorWrapper withModelPackage(final String pkg) {
        this.modelPackage = pkg;
        return this;
    }

    /**
     * Adds the circuit breaker configuration to the generator.
     *
     * @param config a map of class names and their methods that should be configured with circuit breaker
     * @return this wrapper
     */
    public OpenApiClientGeneratorWrapper withCircuitBreakerConfig(final Map<String, List<String>> config) {
        if (config != null) {
            configurator.addAdditionalProperty("circuit-breaker", config);
        }
        return this;
    }

    public OpenApiClientGeneratorWrapper withClassesCodeGenConfig(final Map<String, Object> config) {
        if (config != null) {
            configurator.addAdditionalProperty("classes-codegen", config);
        }
        return this;
    }

    public OpenApiClientGeneratorWrapper withMutiny(final Boolean config) {
        if (config != null) {
            configurator.addAdditionalProperty("mutiny", config);
        }
        return this;
    }

    public OpenApiClientGeneratorWrapper withMutinyReturnTypes(final Map<String, String> returnTypeMappings) {
        if (returnTypeMappings != null && !returnTypeMappings.isEmpty()) {
            Map<String, Object> mutinyOperationIdsMap = new HashMap<>(returnTypeMappings);
            configurator.addAdditionalProperty("mutiny-operation-ids", mutinyOperationIdsMap);
        }
        return this;
    }

    /**
     * Sets the global 'skipFormModel' setting. If not set this setting will default to true.
     *
     * @param skipFormModel whether to skip the generation of models for form parameters
     * @return this wrapper
     */
    public OpenApiClientGeneratorWrapper withSkipFormModelConfig(final String skipFormModel) {
        GlobalSettings.setProperty(CodegenConstants.SKIP_FORM_MODEL, skipFormModel);
        return this;
    }

    public OpenApiClientGeneratorWrapper withTypeMappings(final Map<String, String> typeMappings) {
        typeMappings.forEach(configurator::addTypeMapping);
        return this;
    }

    public OpenApiClientGeneratorWrapper withReturnResponse(Boolean returnResponse) {
        configurator.addAdditionalProperty("return-response", returnResponse);
        return this;
    }

    public OpenApiClientGeneratorWrapper withEnabledSecurityGeneration(Boolean enableSecurityGeneration) {
        configurator.addAdditionalProperty("enable-security-generation", enableSecurityGeneration);
        return this;
    }

    public OpenApiClientGeneratorWrapper withImportMappings(final Map<String, String> typeMappings) {
        typeMappings.forEach(configurator::addImportMapping);
        return this;
    }

    public OpenApiClientGeneratorWrapper withOpenApiNormalizer(final Map<String, String> openApiNormalizer) {
        configurator.setOpenapiNormalizer(openApiNormalizer);
        return this;
    }

    /**
     * Sets the global 'additionalModelTypeAnnotations' setting. If not set this setting will default to empty.
     *
     * @param additionalModelTypeAnnotations the list of extra additional annotations to be included in a model
     * @return this wrapper
     */
    public OpenApiClientGeneratorWrapper withAdditionalModelTypeAnnotationsConfig(final String additionalModelTypeAnnotations) {
        if (additionalModelTypeAnnotations != null) {
            this.configurator.addAdditionalProperty(ADDITIONAL_MODEL_TYPE_ANNOTATIONS, additionalModelTypeAnnotations);
        }
        return this;
    }

    public OpenApiClientGeneratorWrapper withAdditionalEnumTypeUnexpectedMemberConfig(
            final Boolean additionalEnumTypeUnexpectedMember) {
        this.configurator.addAdditionalProperty("additionalEnumTypeUnexpectedMember", additionalEnumTypeUnexpectedMember);
        return this;
    }

    public OpenApiClientGeneratorWrapper withAdditionalEnumTypeUnexpectedMemberNameConfig(
            final String additionalEnumTypeUnexpectedMemberName) {
        this.configurator.addAdditionalProperty("additionalEnumTypeUnexpectedMemberName",
                additionalEnumTypeUnexpectedMemberName);
        return this;
    }

    public OpenApiClientGeneratorWrapper withAdditionalEnumTypeUnexpectedMemberStringValueConfig(
            final String additionalEnumTypeUnexpectedMemberStringValue) {
        this.configurator.addAdditionalProperty("additionalEnumTypeUnexpectedMemberStringValue",
                additionalEnumTypeUnexpectedMemberStringValue);
        return this;
    }

    /**
     * Sets the global 'additionalApiTypeAnnotations' setting. If not set this setting will default to empty.
     *
     * @param additionalApiTypeAnnotations the list of extra additional annotations to be included in an api
     * @return this wrapper
     */
    public OpenApiClientGeneratorWrapper withAdditionalApiTypeAnnotationsConfig(final String additionalApiTypeAnnotations) {
        if (additionalApiTypeAnnotations != null) {
            this.configurator.addAdditionalProperty("additionalApiTypeAnnotations", additionalApiTypeAnnotations.split(";"));
        }
        return this;
    }

    public OpenApiClientGeneratorWrapper withAdditionalRequestArgs(final String additionalRequestArgs) {
        if (additionalRequestArgs != null) {
            this.configurator.addAdditionalProperty("additionalRequestArgs", additionalRequestArgs.split(";"));
        }
        return this;
    }

    public void withTemplateDir(Path templateDir) {
        this.configurator.addAdditionalProperty("templateDir", templateDir.toString());
    }

    public OpenApiClientGeneratorWrapper withGeneratePartFilenameConfig(final Boolean generatePartFilename) {
        this.configurator.addAdditionalProperty("generate-part-filename", generatePartFilename);
        return this;
    }

    public OpenApiClientGeneratorWrapper withPartFilenameValueConfig(final String partFilenameValue) {
        this.configurator.addAdditionalProperty("part-filename-value", partFilenameValue);
        return this;
    }

    public OpenApiClientGeneratorWrapper withUseFieldNameInPartFilenameConfig(final Boolean useFieldNameInPartFilename) {
        this.configurator.addAdditionalProperty("use-field-name-in-part-filename", useFieldNameInPartFilename);
        return this;
    }

    public OpenApiClientGeneratorWrapper withUseBeanValidation(Boolean config) {
        configurator.addAdditionalProperty("use-bean-validation", config);
        return this;
    }

    public OpenApiClientGeneratorWrapper withApiNameSuffix(final String apiNameSuffix) {
        this.configurator.setApiNameSuffix(apiNameSuffix);
        return this;
    }

    public OpenApiClientGeneratorWrapper withModelNameSuffix(final String modelNameSuffix) {
        this.configurator.setModelNameSuffix(modelNameSuffix);
        return this;
    }

    public OpenApiClientGeneratorWrapper withModelNamePrefix(final String modelNamePrefix) {
        this.configurator.setModelNamePrefix(modelNamePrefix);
        return this;
    }

    public List<File> generate(final String basePackage) {
        this.basePackage = basePackage;
        this.consolidatePackageNames();
        return generator.opts(configurator.toClientOptInput()).generate();
    }

    private void consolidatePackageNames() {
        requireNonNull(basePackage);
        if (basePackage.isEmpty()) {
            throw new IllegalArgumentException("basePackage must be a non-empty String");
        }
        if (apiPackage.isEmpty()) {
            this.apiPackage = resolveApiPackage(basePackage);
        }
        if (modelPackage.isEmpty()) {
            this.modelPackage = resolveModelPackage(basePackage);
        }
        this.configurator.setPackageName(basePackage);
        this.configurator.setApiPackage(apiPackage);
        this.configurator.setModelPackage(modelPackage);
        this.configurator.setInvokerPackage(apiPackage);
    }

    public void withConfigKey(final String config) {
        if (config != null && !config.isBlank()) {
            this.configurator.addAdditionalProperty("configKey", StringUtil.replaceNonAlphanumericByUnderscores(config));
        }
    }

    public void withAdditionalPropertiesAsAttribute(final Boolean enable) {
        this.configurator.addAdditionalProperty("additionalPropertiesAsAttribute", Optional.ofNullable(enable).orElse(FALSE));
    }
}
