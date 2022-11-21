package io.quarkiverse.openapi.generator.deployment.codegen;

import java.io.InputStream;

import io.quarkiverse.openapi.generator.deployment.CodegenConfig;
import io.quarkiverse.spec.generator.deployment.codegen.BaseSpecInputModel;

public class SpecInputModel extends BaseSpecInputModel {

    public SpecInputModel(final String fileName, final InputStream inputStream) {
        super(fileName, inputStream);
    }

    public SpecInputModel(final String fileName, final InputStream inputStream, final String basePackageName) {
        super(fileName, inputStream, basePackageName);
    }

    @Override
    protected String getConfigPrefix() {
        return CodegenConfig.CODEGEN_TIME_CONFIG_PREFIX;
    }
}
