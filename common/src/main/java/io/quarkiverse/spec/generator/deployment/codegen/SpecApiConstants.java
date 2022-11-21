package io.quarkiverse.spec.generator.deployment.codegen;

public abstract class SpecApiConstants {

    public static final String YAML = "yaml";
    public static final String YML = "yml";
    public static final String JSON = "json";
    public static final String STREAM = "stream";

    private final String providerPrefix;
    private final String inputDirectory;
    private final String defaultPackage;
    private final String configPrefix;
    private final String extension;

    protected SpecApiConstants(String providerPrefix, String inputDirectory, String defaultPackage, String configPrefix,
            String extension) {
        this.providerPrefix = providerPrefix;
        this.inputDirectory = inputDirectory;
        this.defaultPackage = defaultPackage;
        this.configPrefix = configPrefix;
        this.extension = extension;
    }

    public String getProviderPrefix() {
        return providerPrefix;
    }

    public String getInputDirectory() {
        return inputDirectory;
    }

    public String getDefaultPackage() {
        return defaultPackage;
    }

    public String getConfigPrefix() {
        return configPrefix;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return "XApiConstants [providerPrefix=" + providerPrefix + ", inputDirectory=" + inputDirectory
                + ", defaultPackage=" + defaultPackage + ", configPrefix=" + configPrefix + ", extension=" + extension
                + "]";
    }
}
