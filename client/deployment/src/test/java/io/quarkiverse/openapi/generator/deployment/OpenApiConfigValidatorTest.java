package io.quarkiverse.openapi.generator.deployment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkiverse.openapi.generator.deployment.codegen.OpenApiConfigValidator;
import io.quarkus.bootstrap.prebuild.CodeGenException;

class OpenApiConfigValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "quarkus.openapi-generator.codegen.additional-api-type-annotations",
            "quarkus.openapi-generator.codegen.spec.spec_yaml.enable-security-generation",
            "quarkus.openapi-generator.codegen.type-mappings.UUID=String",
            "quarkus.openapi-generator.codegen.spec.spec_yaml.type-mappings.UUID=String",
            "quarkus.openapi-generator.codegen.spec.spec_yaml.generate-apis=false",
            "quarkus.openapi-generator.codegen.spec.spec_yaml.generate-models=false",
            "quarkus.openapi-generator.codegen.spec.spec_yaml.generate-apis=true",
            "quarkus.openapi-generator.codegen.spec.spec_yaml.generate-models=true",
    })
    void test_known_configs_ok(String validConfiguration) {
        assertThatCode(() -> OpenApiConfigValidator.validateInputConfiguration(List.of(validConfiguration)))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "quarkus.openapi-generator.codegen.eenable-security-generation",
            "quarkus.openapi-generator.codegen.spec.spec_yaml.eenable-security-generation" })
    void test_unknown_config_typo(String invalidConfiguration) {
        assertThatExceptionOfType(CodeGenException.class)
                .isThrownBy(() -> OpenApiConfigValidator
                        .validateInputConfiguration(List.of(invalidConfiguration)))
                .withMessageStartingWith("Found unsupported configuration: [eenable-security-generation]");
    }
}
