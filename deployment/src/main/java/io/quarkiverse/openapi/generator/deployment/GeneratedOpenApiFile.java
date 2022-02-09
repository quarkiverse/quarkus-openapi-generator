package io.quarkiverse.openapi.generator.deployment;

import org.jboss.jandex.ClassInfo;

import io.quarkus.builder.item.MultiBuildItem;

public abstract class GeneratedOpenApiFile extends MultiBuildItem {
    private final ClassInfo classInfo;

    public GeneratedOpenApiFile(final ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }
}
