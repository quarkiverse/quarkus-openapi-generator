package io.quarkiverse.openapi.generator.deployment.codegen;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.smallrye.config.SmallRyeConfigBuilder;

public class OpenApiGeneratorStreamCodeGen extends OpenApiGeneratorCodeGenBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiGeneratorStreamCodeGen.class);

    private final List<OpenApiSpecInputProvider> providers;

    public OpenApiGeneratorStreamCodeGen() {
        ServiceLoader<OpenApiSpecInputProvider> loader = ServiceLoader.load(OpenApiSpecInputProvider.class);
        providers = loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
        LOGGER.debug("Loaded {} OpenApiSpecInputProviders", providers);
    }

    @Override
    public String providerId() {
        return OpenApiGeneratorOutputPaths.STREAM_PATH;
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
            for (SpecInputModel inputModel : provider.read(context)) {
                LOGGER.debug("Processing OpenAPI spec input model {}", inputModel);
                if (inputModel == null) {
                    throw new CodeGenException("SpecInputModel from provider " + provider + " is null");
                }
                try {
                    final Path openApiFilePath = Paths.get(outDir.toString(), inputModel.getFileName());
                    Files.createDirectories(openApiFilePath.getParent());
                    try (ReadableByteChannel inChannel = Channels.newChannel(inputModel.getInputStream());
                            FileChannel outChannel = FileChannel.open(openApiFilePath, StandardOpenOption.WRITE,
                                    StandardOpenOption.CREATE)) {
                        outChannel.transferFrom(inChannel, 0, Integer.MAX_VALUE);
                        LOGGER.debug("Saved OpenAPI spec input model in {}", openApiFilePath);
                        this.generate(this.mergeConfig(context, inputModel), openApiFilePath, outDir);
                        generated = true;
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to save InputStream from provider " + provider + " into location ",
                            e);
                }
            }
        }
        return generated;
    }

    private Config mergeConfig(CodeGenContext context, SpecInputModel inputModel) {
        final List<ConfigSource> sources = new ArrayList<>();
        context.config().getConfigSources().forEach(sources::add);
        return new SmallRyeConfigBuilder()
                .withSources(inputModel.getConfigSource())
                .withSources(sources).build();
    }

    @Override
    public boolean shouldRun(Path sourceDir, Config config) {
        return !this.providers.isEmpty();
    }
}
