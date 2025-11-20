package io.quarkiverse.openapi.generator.deployment.codegen;

import java.io.InputStream;

/**
 * Represents a model for a zipped specification input.
 * This class extends the {@code SpecInputModel} class and provides additional support
 * for handling openAPI specifications within the zipped input source (jar/zip).
 */
public class ZippedSpecInputModel extends SpecInputModel {

    private final String rootFileOfSpec;

    public ZippedSpecInputModel(final String filename, final String rootFileOfSpec, final InputStream inputStream) {
        super(filename, inputStream);
        this.rootFileOfSpec = rootFileOfSpec;
    }

    public ZippedSpecInputModel(final String filename, final String rootFileOfSpec, final InputStream inputStream,
            final String basePackageName) {
        super(filename, inputStream, basePackageName);
        this.rootFileOfSpec = rootFileOfSpec;
    }

    public ZippedSpecInputModel(final String filename, final String rootFileOfSpec, final InputStream inputStream,
            final String basePackageName,
            final String apiNameSuffix, final String modelNameSuffix, final String modelNamePrefix) {
        super(filename, inputStream, basePackageName, apiNameSuffix, modelNameSuffix, modelNamePrefix);
        this.rootFileOfSpec = rootFileOfSpec;
    }

    /**
     * Retrieves the root file name of the specification associated with this model.
     *
     * @return the root file name of the specification.
     */
    public String getRootFileOfSpec() {
        return rootFileOfSpec;
    }
}
