package io.quarkiverse.openapi.server.generator.deployment.codegen;

import java.nio.file.Path;

public record ServerCodegenSpec(
        String configKey,
        Path inputBaseDir,
        Path specPath,
        String basePackage,
        boolean reactive,
        boolean builders,
        boolean beanValidation) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String configKey;
        private Path inputBaseDir;
        private Path specPath;
        private String basePackage;
        private boolean reactive;
        private boolean builders;
        private boolean beanValidation;

        private Builder() {
        }

        public Builder configKey(String configKey) {
            this.configKey = configKey;
            return this;
        }

        public Builder inputBaseDir(Path inputBaseDir) {
            this.inputBaseDir = inputBaseDir;
            return this;
        }

        public Builder specPath(Path specPath) {
            this.specPath = specPath;
            return this;
        }

        public Builder basePackage(String basePackage) {
            this.basePackage = basePackage;
            return this;
        }

        public Builder reactive(boolean reactive) {
            this.reactive = reactive;
            return this;
        }

        public Builder builders(boolean builders) {
            this.builders = builders;
            return this;
        }

        public Builder beanValidation(boolean beanValidation) {
            this.beanValidation = beanValidation;
            return this;
        }

        public ServerCodegenSpec build() {
            return new ServerCodegenSpec(configKey, inputBaseDir, specPath, basePackage, reactive, builders,
                    beanValidation);
        }
    }
}
