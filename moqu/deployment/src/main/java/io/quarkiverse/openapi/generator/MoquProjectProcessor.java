package io.quarkiverse.openapi.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.jboss.logging.Logger;

import io.quarkiverse.openapi.generator.items.MoquProjectBuildItem;
import io.quarkiverse.openapi.generator.moqu.MoquConfig;
import io.quarkiverse.openapi.generator.moqu.recorder.MoquRoutesRecorder;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.runtime.util.ClassPathUtils;
import io.quarkus.vertx.http.deployment.RouteBuildItem;

public class MoquProjectProcessor {

    private static final Logger LOGGER = Logger.getLogger(MoquProjectProcessor.class);

    @BuildStep
    MoquProjectBuildItem generate(MoquConfig config) {
        try {

            HashMap<String, String> filesMap = new HashMap<>();
            ClassPathUtils.consumeAsPaths(config.resourceDir(), path -> {
                try {
                    boolean directory = Files.isDirectory(path);
                    if (directory) {
                        try (Stream<Path> pathStream = Files.find(path, Integer.MAX_VALUE,
                                (p, a) -> Files.isRegularFile(p) && p.getFileName().toString().endsWith(".yaml"))) {
                            pathStream.forEach(p -> {
                                try {
                                    filesMap.put(p.getFileName().toString(), Files.readString(p));
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
    @Record(ExecutionTime.RUNTIME_INIT)
    void consume(Optional<MoquProjectBuildItem> moquProject,
            MoquConfig config,
            BuildProducer<RouteBuildItem> routes,
            MoquRoutesRecorder recorder) {

        moquProject.ifPresent(project -> {
            for (Map.Entry<String, String> spec : project.specs().entrySet()) {
                routes.produce(RouteBuildItem.builder()
                        .routeFunction(config.moquBase() + spec.getKey(),
                                recorder.handleFile(spec.getValue()))
                        .build());
            }
        });
    }
}
