package io.quarkiverse.openapi.generator;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class MoquWiremockProcessor {

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("moqu-wiremock");
    }
}
