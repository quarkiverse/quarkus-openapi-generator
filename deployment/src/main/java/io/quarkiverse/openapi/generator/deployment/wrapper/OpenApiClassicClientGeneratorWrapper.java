package io.quarkiverse.openapi.generator.deployment.wrapper;

import java.nio.file.Path;

public class OpenApiClassicClientGeneratorWrapper extends OpenApiClientGeneratorWrapper {

    public OpenApiClassicClientGeneratorWrapper(Path specFilePath, Path outputDir, boolean verbose, boolean validateSpec) {
        super(createConfigurator(), specFilePath, outputDir, verbose, validateSpec);
    }

    private static QuarkusCodegenConfigurator createConfigurator() {
        QuarkusCodegenConfigurator configurator = new QuarkusCodegenConfigurator();
        configurator.addAdditionalProperty("is-resteasy-reactive", false);
        return configurator;
    }
}
