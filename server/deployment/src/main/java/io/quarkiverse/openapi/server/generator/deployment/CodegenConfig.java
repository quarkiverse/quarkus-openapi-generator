package io.quarkiverse.openapi.server.generator.deployment;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX, phase = ConfigPhase.BUILD_TIME)
public class CodegenConfig {

    static final String CODEGEN_TIME_CONFIG_PREFIX = "quarkus.openapi.generator";
    private static final String CODEGEN_BASE_PACKAGE = CODEGEN_TIME_CONFIG_PREFIX + ".base-package";
    private static final String CODEGEN_SPEC = CODEGEN_TIME_CONFIG_PREFIX + ".spec";
    private static final String INPUT_BASE_DIR = CODEGEN_TIME_CONFIG_PREFIX + ".input-base-dir";
    private static final String CODEGEN_REACTIVE = CODEGEN_TIME_CONFIG_PREFIX + ".reactive";

    public static String getBasePackagePropertyName() {
        return CODEGEN_BASE_PACKAGE;
    }

    public static String getSpecPropertyName() {
        return CODEGEN_SPEC;
    }

    public static String getInputBaseDirPropertyName() {
        return INPUT_BASE_DIR;
    }

    public static String getCodegenReactive() {
        return CODEGEN_REACTIVE;
    }
}
