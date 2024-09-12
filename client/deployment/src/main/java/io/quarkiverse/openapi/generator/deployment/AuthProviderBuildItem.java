package io.quarkiverse.openapi.generator.deployment;

import io.quarkus.builder.item.MultiBuildItem;

public final class AuthProviderBuildItem extends MultiBuildItem {

    final String openApiSpecId;
    final String name;

    AuthProviderBuildItem(String openApiSpecId, String name) {
        this.openApiSpecId = openApiSpecId;
        this.name = name;
    }

    public String getOpenApiSpecId() {
        return openApiSpecId;
    }

    public String getName() {
        return name;
    }
}
