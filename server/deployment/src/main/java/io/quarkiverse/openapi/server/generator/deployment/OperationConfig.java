package io.quarkiverse.openapi.server.generator.deployment;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithName;

@ConfigGroup
public interface OperationConfig {

    /**
     * The name of the method parameter that should be used to return the response from the operation.
     */
    @WithName("return-type")
    String returnType();
}
