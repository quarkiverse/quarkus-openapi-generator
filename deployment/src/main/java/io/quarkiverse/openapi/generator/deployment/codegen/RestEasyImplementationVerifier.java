package io.quarkiverse.openapi.generator.deployment.codegen;

import java.util.Collection;

final class RestEasyImplementationVerifier {

    private static final RestEasyImplementationVerifier INSTANCE = new RestEasyImplementationVerifier();

    static final String RESTEASY_REACTIVE_ARTIFACT_ID = "resteasy-reactive";

    static final String RESTEASY_CLASSIC_ARTIFACT_ID = "quarkus-resteasy-common";

    private RestEasyImplementationVerifier() {
    }

    static RestEasyImplementationVerifier get() {
        return INSTANCE;
    }

    boolean isRestEasyReactive(Collection<String> artifactIds) {
        for (String artifactId : artifactIds) {
            if (RESTEASY_REACTIVE_ARTIFACT_ID.equals(artifactId)) {
                return true;
            } else if (RESTEASY_CLASSIC_ARTIFACT_ID.equals(artifactId)) {
                return false;
            }
        }

        throw new IllegalStateException(
                "It was not possible to identify if the application is using RESTEasy classic or reactive");
    }
}
