package io.quarkiverse.openapi.generator.deployment.wrapper;

import java.nio.file.Path;

public class OpenApiReactiveClientGeneratorWrapper extends OpenApiClientGeneratorWrapper {

    public OpenApiReactiveClientGeneratorWrapper(Path specFilePath, Path outputDir, boolean verbose, boolean validateSpec) {
        super(createConfigurator(), specFilePath, outputDir, verbose, validateSpec);
    }

    private static QuarkusCodegenConfigurator createConfigurator() {
        QuarkusCodegenConfigurator configurator = new QuarkusCodegenConfigurator();
        configurator.addAdditionalProperty("is-resteasy-reactive", true);
        return configurator;
    }
}
