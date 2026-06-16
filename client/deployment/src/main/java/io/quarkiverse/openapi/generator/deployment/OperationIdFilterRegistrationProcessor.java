package io.quarkiverse.openapi.generator.deployment;

import io.quarkiverse.openapi.generator.providers.OperationIdFilter;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

/**
 * Quarkus build step to ensure OperationIdFilter is available as a CDI bean.
 * This is needed for RESTEasy Reactive which may not honor @RegisterProvider annotations
 * on provider classes referenced in REST client interfaces.
 */
public class OperationIdFilterRegistrationProcessor {

    private static final String FEATURE = "openapi-generator-operation-id-filter";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem registerOperationIdFilter() {
        return AdditionalBeanBuildItem.unremovableOf(OperationIdFilter.class);
    }
}
