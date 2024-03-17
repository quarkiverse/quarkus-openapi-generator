package io.quarkiverse.openapi.wiremock.generator.deployment.codegen;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.wiremock.generator.deployment.wrapper.OpenApiWiremockGeneratorWrapper;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

/**
 * OpenAPI Wiremock Stubbing Generator
 */
public class OpenApiWiremockCodegen implements CodeGenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiWiremockCodegen.class);
    public static final String WIREMOCK = "wiremock";
    public static final String INPUT_DIR = "openapi";

    @Override
    public String providerId() {
        return WIREMOCK;
    }

    @Override
    public String inputDirectory() {
        return INPUT_DIR;
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        LOGGER.info("Generating Wiremock Stubbing");
        OpenApiWiremockGeneratorWrapper wrapper = new OpenApiWiremockGeneratorWrapper(context.inputDir(), context.outDir());
        try {
            wrapper.generate();
            LOGGER.info("Wiremock Stubbing generated successfully!");
            return true;
        } catch (CodeGenException | IOException e) {
            LOGGER.error("Wiremock Stubbing was not generated!", e);
            throw new CodeGenException(e);
        }
    }

}
