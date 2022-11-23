package io.quarkiverse.openapi.generator.deployment.codegen;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.microprofile.config.Config;

import io.quarkiverse.openapi.generator.deployment.CodegenConfig;
import io.quarkiverse.spec.generator.deployment.codegen.SpecApiGeneratorCodeGenBase;

public abstract class OpenApiGeneratorCodeGenBase extends SpecApiGeneratorCodeGenBase {

    protected OpenApiGeneratorCodeGenBase(String extension) {
        super(new OpenApiGeneratorCodeGenerator(), new OpenApiParameters(extension));
    }

    @Override
    protected Collection<String> excludedFiles(final Config config) {
        return config.getOptionalValues(CodegenConfig.EXCLUDED_FILES_PROP_FORMAT, String.class).orElse(Collections.emptyList());
    }
}
