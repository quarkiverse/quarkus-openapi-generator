package io.quarkiverse.openapi.wiremock.generator.deployment;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = CodegenConfig.OPENAPI_WIREMOCK_PREFIX, phase = ConfigPhase.BUILD_TIME)
public class CodegenConfig {
    static final String OPENAPI_WIREMOCK_PREFIX = "quarkus.openapi.generator.wiremock";
}