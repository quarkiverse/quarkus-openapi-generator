package io.quarkiverse.openapi.generator.deployment;

import io.quarkus.arc.deployment.ValidationPhaseBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.runtime.configuration.ConfigurationException;

class CodeGenConfigurationProcessor {

    private static final String CONFIG_MISMATCH_ERROR_MSG = "The property 'quarkus.openapi-generator.codegen.rest-client-reactive' needs to be set to '%s' to be compatible with '%s'!";

    @BuildStep
    public void checkCompatibility(Capabilities capabilities, CodegenConfig codegenConfig,
            BuildProducer<ValidationPhaseBuildItem.ValidationErrorBuildItem> validationErrors) {

        boolean restClientReactive = capabilities.isPresent(Capability.REST_CLIENT_REACTIVE);
        boolean restClientClassic = capabilities.isPresent(Capability.REST_CLIENT);

        if (!restClientClassic && !restClientReactive) {
            validationErrors.produce(createErrorBuildItem(
                    "The openapi-generator needs at least a REST Client Classic or Reactive extension installed. Please be aware, that the respective jackson extensions are required as well!"));
        }

        if (restClientClassic && restClientReactive) {
            validationErrors.produce(createErrorBuildItem(
                    "The openapi-generator cannot work with both REST Client Classic and Reactive extensions at the same time!"));
        }

        if (restClientReactive && !codegenConfig.restClientReactive) {
            validationErrors.produce(
                    createErrorBuildItem(String.format(CONFIG_MISMATCH_ERROR_MSG, "true", "rest-client-reactive")));
        }

        if (restClientClassic && codegenConfig.restClientReactive) {
            validationErrors.produce(
                    createErrorBuildItem(String.format(CONFIG_MISMATCH_ERROR_MSG, "false", "rest-client")));
        }

    }

    private static ValidationPhaseBuildItem.ValidationErrorBuildItem createErrorBuildItem(String message) {
        return new ValidationPhaseBuildItem.ValidationErrorBuildItem(new ConfigurationException(message));

    }
}
