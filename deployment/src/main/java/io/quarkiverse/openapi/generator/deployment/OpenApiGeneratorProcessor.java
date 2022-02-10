package io.quarkiverse.openapi.generator.deployment;

import org.jboss.jandex.ClassInfo;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class OpenApiGeneratorProcessor {

    private static final String FEATURE = "openapi-generator";

    OpenApiGeneratorConfiguration configuration;

    @BuildStep
    void discoverGeneratedApis(BuildProducer<GeneratedOpenApiRestClientBuildItem> restClients,
            BuildProducer<GeneratedOpenApiModelBuildItem> models,
            BuildProducer<FeatureBuildItem> features,
            CombinedIndexBuildItem index) {

        boolean hasGeneratedFiles = false;
        for (SpecConfig spec : configuration.specs.values()) {
            for (ClassInfo classInfo : index.getIndex().getKnownClasses()) {
                if (classInfo.name().packagePrefix().equals(spec.getApiPackage())) {
                    restClients.produce(new GeneratedOpenApiRestClientBuildItem(classInfo));
                    hasGeneratedFiles = true;
                } else if (classInfo.name().packagePrefix().equals(spec.getModelPackage())) {
                    models.produce(new GeneratedOpenApiModelBuildItem(classInfo));
                    hasGeneratedFiles = true;
                }
            }
        }

        if (hasGeneratedFiles) {
            features.produce(new FeatureBuildItem(FEATURE));
        }
    }
}
