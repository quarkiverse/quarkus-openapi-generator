package io.quarkiverse.openapi.generator.deployment;

public enum CodegenConfigName {
    //global configs
    VERBOSE("verbose"),
    INPUT_BASE_DIR("input-base-dir"),
    INCLUDE("include"),
    EXCLUDE("exclude"),
    VALIDATE_SPEC("validateSpec"),
    DEFAULT_SECURITY_SCHEME("default-security-scheme"),

    //spec configs only
    BASE_PACKAGE("base-package"),
    API_NAME_SUFFIX("api-name-suffix"),
    MODEL_NAME_SUFFIX("model-name-suffix"),
    MODEL_NAME_PREFIX("model-name-prefix"),

    //global & spec configs
    SKIP_FORM_MODEL("skip-form-model"),
    MUTINY("mutiny"),
    MUTINY_OPERATION_IDS("mutiny.operation-ids"),
    ADDITIONAL_MODEL_TYPE_ANNOTATIONS("additional-model-type-annotations"),
    ADDITIONAL_ENUM_TYPE_UNEXPECTED_MEMBER("additional-enum-type-unexpected-member"),
    ADDITIONAL_ENUM_TYPE_UNEXPECTED_MEMBER_NAME("additional-enum-type-unexpected-member-name"),
    ADDITIONAL_ENUM_TYPE_UNEXPECTED_MEMBER_STRING_VALUE("additional-enum-type-unexpected-member-string-value"),
    ADDITIONAL_API_TYPE_ANNOTATIONS("additional-api-type-annotations"),
    TYPE_MAPPINGS("type-mappings"),
    IMPORT_MAPPINGS("import-mappings"),
    NORMALIZER("open-api-normalizer"),
    RETURN_RESPONSE("return-response"),
    ENABLE_SECURITY_GENERATION("enable-security-generation"),
    CONFIG_KEY("config-key"),
    GENERATE_PART_FILENAME("generate-part-filename"),
    PART_FILENAME_VALUE("part-filename-value"),
    USE_FIELD_NAME_IN_PART_FILENAME("use-field-name-in-part-filename"),
    ADDITIONAL_PROPERTIES_AS_ATTRIBUTE("additional-properties-as-attribute"),
    ADDITIONAL_REQUEST_ARGS("additional-request-args"),
    BEAN_VALIDATION("use-bean-validation");

    public final String name;

    CodegenConfigName(String name) {
        this.name = name;
    }
}