package io.quarkiverse.openapi.generator.deployment.codegen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.codegen.OpenApiSpecInputProvider;
import io.quarkiverse.openapi.generator.codegen.SpecInputModel;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;

public class OpenApiGeneratorStreamCodeGen extends OpenApiGeneratorCodeGenBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiGeneratorStreamCodeGen.class);

    private List<OpenApiSpecInputProvider> providers;
    private final ServiceLoader<OpenApiSpecInputProvider> loader;

    public OpenApiGeneratorStreamCodeGen() {
        loader = ServiceLoader.load(OpenApiSpecInputProvider.class);
    }

    private void loadServices() {
        loader.reload();
        providers = loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
        LOGGER.debug("Loaded {} OpenApiSpecInputProviders", providers);
    }

    @Override
    public String providerId() {
        return "open-api-stream";
    }

    // unused by this CodeGenProvider since we rely on the input coming from ServiceLoaders
    @Override
    public String inputExtension() {
        return ".yaml";
    }

    // unused by this CodeGenProvider since we rely on the input coming from ServiceLoaders
    @Override
    public String inputDirectory() {
        return "openapi";
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        final Path outDir = context.outDir();
        boolean generated = false;

        for (final OpenApiSpecInputProvider provider : this.providers) {
            for (SpecInputModel inputModel : provider.read()) {
                LOGGER.debug("Processing OpenAPI spec input model {}", inputModel);
                if (inputModel == null) {
                    throw new CodeGenException("SpecInputModel from provider " + provider + " is null");
                }
                final Path openApiFilePath = Paths.get(outDir.toString(), inputModel.getFileName());
                try (ReadableByteChannel channel = Channels.newChannel(inputModel.getInputStream());
                        FileOutputStream output = new FileOutputStream(openApiFilePath.toString())) {
                    output.getChannel().transferFrom(channel, 0, Integer.MAX_VALUE);
                    LOGGER.debug("Saved OpenAPI spec input model in {}", openApiFilePath);
                    this.generate(context, openApiFilePath, outDir);
                    generated = true;
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to save InputStream from provider " + provider + " into location ",
                            e);
                }
            }
        }
        return generated;
    }

    @Override
    public boolean shouldRun(Path sourceDir, Config config) {
        this.loadServices();
        return !this.providers.isEmpty();
    }
}
