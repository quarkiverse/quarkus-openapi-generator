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
}
