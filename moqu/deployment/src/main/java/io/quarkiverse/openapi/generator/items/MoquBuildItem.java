package io.quarkiverse.openapi.generator.items;

import io.quarkiverse.openapi.moqu.Moqu;
import io.quarkus.builder.item.MultiBuildItem;

public final class MoquBuildItem extends MultiBuildItem {

    private final String filename;
    private final String extension;
    private final Moqu moqu;

    public MoquBuildItem(String filename, String extension, Moqu moqu) {
        this.filename = filename;
        this.extension = extension;
        this.moqu = moqu;
    }

    public String getFilename() {
        return filename;
    }

    public String getExtension() {
        return extension;
    }

    public Moqu getMoqu() {
        return moqu;
    }

    public String getFullFilename() {
        return filename + "." + extension;
    }

    public String prefixUri(String basePath) {
        return String.format("%s/%s/%s", basePath, extension, filename);
    }
}
