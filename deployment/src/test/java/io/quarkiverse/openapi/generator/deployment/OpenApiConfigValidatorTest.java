package io.quarkiverse.openapi.generator.deployment;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.deployment.codegen.OpenApiConfigValidator;
import io.quarkus.bootstrap.prebuild.CodeGenException;

public class OpenApiConfigValidatorTest {

    @Test
    public void test_known_configs_ok() throws CodeGenException {
        OpenApiConfigValidator
                .validateInputConfiguration(List.of("quarkus.openapi-generator.codegen.additional-api-type-annotations"));
        OpenApiConfigValidator.validateInputConfiguration(
                List.of("quarkus.openapi-generator.codegen.spec.spec_yaml.enable-security-generation"));
        OpenApiConfigValidator.validateInputConfiguration(
                List.of("quarkus.openapi-generator.codegen.type-mappings.UUID=String"));
        OpenApiConfigValidator.validateInputConfiguration(
                List.of("quarkus.openapi-generator.codegen.spec.spec_yaml.type-mappings.UUID=String"));
    }

    @Test
    public void test_uknown_config_typo() {
        CodeGenException codeGenException = Assertions.assertThrows(CodeGenException.class, () -> {
            OpenApiConfigValidator
                    .validateInputConfiguration(List.of("quarkus.openapi-generator.codegen.eenable-security-generation"));
        });

        Assertions.assertTrue(
                codeGenException.getMessage().startsWith("Found unsupported configuration: [eenable-security-generation]"),
                codeGenException.getMessage());

        codeGenException = Assertions.assertThrows(CodeGenException.class, () -> {
            OpenApiConfigValidator.validateInputConfiguration(
                    List.of("quarkus.openapi-generator.codegen.spec.spec_yaml.eenable-security-generation"));
        });

        Assertions.assertTrue(
                codeGenException.getMessage().startsWith("Found unsupported configuration: [eenable-security-generation]"),
                codeGenException.getMessage());
    }
}
