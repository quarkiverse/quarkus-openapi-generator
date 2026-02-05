package io.quarkiverse.openapi.server.generator.deployment;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX)
public interface CodegenConfig extends ServerCodegenConfig {

    String CODEGEN_TIME_CONFIG_PREFIX = "quarkus.openapi.generator";
    String CODEGEN_BASE_PACKAGE = CODEGEN_TIME_CONFIG_PREFIX + ".base-package";
    String CODEGEN_SPEC = CODEGEN_TIME_CONFIG_PREFIX + ".spec";
    String INPUT_BASE_DIR = CODEGEN_TIME_CONFIG_PREFIX + ".input-base-dir";
    String CODEGEN_REACTIVE = CODEGEN_TIME_CONFIG_PREFIX + ".reactive";
    String GENERATE_BUILDERS = CODEGEN_TIME_CONFIG_PREFIX + ".builders";
    String CODEGEN_BEAN_VALIDATION = CODEGEN_TIME_CONFIG_PREFIX + ".use-bean-validation";

    // all new properties related to server generator must be prefixed with 'server'.
    String SERVER = ".server";
    String CODEGEN_SERVER_USE = CODEGEN_TIME_CONFIG_PREFIX + SERVER + ".use";
    String CODEGEN_SERVER_BASE_PACKAGE = CODEGEN_TIME_CONFIG_PREFIX + SERVER + ".base-package";
    String CODEGEN_SERVER_SPEC = CODEGEN_TIME_CONFIG_PREFIX + SERVER + ".spec";
    String CODEGEN_SERVER_INPUT_BASE_DIR = CODEGEN_TIME_CONFIG_PREFIX + SERVER + ".input-base-dir";
    String CODEGEN_SERVER_REACTIVE = CODEGEN_TIME_CONFIG_PREFIX + SERVER + ".use-reactive";
    String CODEGEN_SERVER_GENERATE_BUILDERS = CODEGEN_TIME_CONFIG_PREFIX + SERVER + ".use-builder";
    String CODEGEN_SERVER_BEAN_VALIDATION = CODEGEN_TIME_CONFIG_PREFIX + SERVER + ".use-bean-validation";

    static String getBasePackagePropertyName() {
        return CODEGEN_BASE_PACKAGE;
    }

    static String getSpecPropertyName() {
        return CODEGEN_SPEC;
    }

    static String getInputBaseDirPropertyName() {
        return INPUT_BASE_DIR;
    }

    static String getCodegenReactive() {
        return CODEGEN_REACTIVE;
    }

    static String getGenerateBuilders() {
        return GENERATE_BUILDERS;
    }

    static String getUseBeanValidation() {
        return CODEGEN_BEAN_VALIDATION;
    }

    // under .server
    static String getServerBasePackagePropertyName() {
        return CODEGEN_SERVER_BASE_PACKAGE;
    }

    static String getServerSpecPropertyName() {
        return CODEGEN_SERVER_SPEC;
    }

    static String getServerInputBaseDirPropertyName() {
        return CODEGEN_SERVER_INPUT_BASE_DIR;
    }

    static String getServerCodegenReactive() {
        return CODEGEN_SERVER_REACTIVE;
    }

    static String getServerGenerateBuilders() {
        return CODEGEN_SERVER_GENERATE_BUILDERS;
    }

    static String getServerUseBeanValidation() {
        return CODEGEN_SERVER_BEAN_VALIDATION;
    }

    /**
     * Indicates if it should use 'apicurio' or 'openapitools' generator.
     */
    static String getServerUse() {
        return CODEGEN_SERVER_USE;
    }
}
