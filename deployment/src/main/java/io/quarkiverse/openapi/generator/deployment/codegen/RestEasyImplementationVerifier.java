package io.quarkiverse.openapi.generator.deployment.codegen;

import java.util.Collection;

final class RestEasyImplementationVerifier {

    private static final RestEasyImplementationVerifier INSTANCE = new RestEasyImplementationVerifier();

    private static final String BOTH_DEPENDENCIES_PRESENT_ERROR_MSG = "The application has RESTEasy classic and reactive on its dependencies. Both dependencies cannot coexists. You need to remove one of them.";

    static final String RESTEASY_REACTIVE_ARTIFACT_ID = "resteasy-reactive";

    static final String RESTEASY_CLASSIC_ARTIFACT_ID = "quarkus-resteasy-common";

    private RestEasyImplementationVerifier() {
    }

    static RestEasyImplementationVerifier get() {
        return INSTANCE;
    }

    boolean isRestEasyReactive(Collection<String> artifactIds) {
        boolean foundReactive = false;
        boolean foundClassic = false;

        for (String artifactId : artifactIds) {
            if (RESTEASY_REACTIVE_ARTIFACT_ID.equals(artifactId)) {
                foundReactive = true;
                if (foundClassic) {
                    throw new IllegalStateException(BOTH_DEPENDENCIES_PRESENT_ERROR_MSG);
                }
            } else if (RESTEASY_CLASSIC_ARTIFACT_ID.equals(artifactId)) {
                foundClassic = true;
                if (foundReactive) {
                    throw new IllegalStateException(BOTH_DEPENDENCIES_PRESENT_ERROR_MSG);
                }
            }
        }

        if (foundReactive) {
            return true;
        } else if (foundClassic) {
            return false;
        } else {
            throw new IllegalStateException(
                    "It was not possible to identify if the application is using RESTEasy classic or reactive");
        }
    }
}
