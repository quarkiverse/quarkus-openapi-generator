package io.quarkiverse.openapi.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.jboss.logging.Logger;

import io.quarkiverse.openapi.generator.items.MoquBuildItem;
import io.quarkiverse.openapi.generator.items.MoquProjectBuildItem;
import io.quarkiverse.openapi.generator.moqu.MoquConfig;
import io.quarkiverse.openapi.moqu.Moqu;
import io.quarkiverse.openapi.moqu.OpenAPIMoquImporter;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.runtime.util.ClassPathUtils;

public class MoquProjectProcessor {

    private static final Logger LOGGER = Logger.getLogger(MoquProjectProcessor.class);

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("yaml", "yml", "json");

    @BuildStep
    MoquProjectBuildItem generate(MoquConfig config) {
        try {

            HashMap<String, MoquProjectBuildItem.File> filesMap = new HashMap<>();
            ClassPathUtils.consumeAsPaths(config.resourceDir(), path -> {
                try {
                    boolean directory = Files.isDirectory(path);
                    if (directory) {
                        try (Stream<Path> pathStream = Files.find(path, Integer.MAX_VALUE,
                                (p, a) -> Files.isRegularFile(p) && SUPPORTED_EXTENSIONS.contains(
                                        getExtension(p.getFileName().toString())))) {

                            pathStream.forEach(p -> {
                                try {
                                    String filename = p.getFileName().toString();

                                    MoquProjectBuildItem.File moquFile = new MoquProjectBuildItem.File(
                                            removeExtension(filename), getExtension(filename), Files.readString(p));

                                    filesMap.put(filename, moquFile);

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            return new MoquProjectBuildItem(filesMap);

        } catch (IOException e) {
            LOGGER.error("Was not possible to scan Moqu project files.", e);
            throw new RuntimeException(e);
        }
    }

    @BuildStep(onlyIf = { IsDevelopment.class })
    void consume(Optional<MoquProjectBuildItem> moquProject,
            BuildProducer<MoquBuildItem> moquMocks) {

        OpenAPIMoquImporter importer = new OpenAPIMoquImporter();
        moquProject.ifPresent(project -> {
            for (Map.Entry<String, MoquProjectBuildItem.File> spec : project.specs().entrySet()) {

                MoquProjectBuildItem.File moquFile = spec.getValue();

                Moqu moqu = importer.parse(moquFile.content());

                moquMocks.produce(new MoquBuildItem(
                        moquFile.filename(),
                        moquFile.extension(),
                        moqu));
            }
        });
    }

    public static String getExtension(String path) {
        Objects.requireNonNull(path, "path is required");
        final int i = path.lastIndexOf(".");
        return i > 0 ? path.substring(i + 1) : null;
    }

    public static String removeExtension(String path) {
        Objects.requireNonNull(path, "path is required");
        final int i = path.lastIndexOf(".");
        return i > 0 ? path.substring(0, i) : path;
    }
}
