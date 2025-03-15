package io.quarkiverse.openapi.generator.items;

import java.util.Collections;
import java.util.Map;

import io.quarkus.builder.item.SimpleBuildItem;

public final class MoquProjectBuildItem extends SimpleBuildItem {

    private final Map<String, File> specs;

    public MoquProjectBuildItem(Map<String, File> specs) {
        this.specs = specs;
    }

    public Map<String, File> specs() {
        return Collections.unmodifiableMap(specs);
    }

    public record File(String filename, String extension, String content) {
    }
}
