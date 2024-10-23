package io.quarkiverse.openapi.generator.items;

import java.util.Collections;
import java.util.Map;

import io.quarkus.builder.item.SimpleBuildItem;

public final class MoquProjectBuildItem extends SimpleBuildItem {

    private final Map<String, String> specs;

    public MoquProjectBuildItem(Map<String, String> specs) {
        this.specs = specs;
    }

    public Map<String, String> specs() {
        return Collections.unmodifiableMap(specs);
    }
}
