package io.quarkiverse.openapi.server.generator.deployment.codegen;

import org.eclipse.microprofile.config.Config;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurableGenerationConfig extends DefaultGenerationConfig {

    private static final Logger log = LoggerFactory.getLogger(ConfigurableGenerationConfig.class);

    private final boolean generateBuilders;

    public ConfigurableGenerationConfig(Config config) {
        generateBuilders = config
                .getOptionalValue("quarkus.openapi.generator.options.generate-builders", Boolean.class)
                .orElse(Boolean.FALSE);
        log.info("generateBuilders={}", generateBuilders);
    }

    @Override
    public boolean isGenerateBuilders() {
        return generateBuilders;
    }

}
