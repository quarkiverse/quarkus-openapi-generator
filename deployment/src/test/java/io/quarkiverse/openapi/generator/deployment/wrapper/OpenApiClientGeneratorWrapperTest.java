package io.quarkiverse.openapi.generator.deployment.wrapper;

import static io.quarkiverse.openapi.generator.deployment.SpecConfig.getBuildTimeSpecPropertyPrefix;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

public class OpenApiClientGeneratorWrapperTest {

    @Test
    void generatePetStore() throws URISyntaxException {
        final Path petstoreOpenApi = Path
                .of(requireNonNull(this.getClass().getResource("/openapi/petstore-openapi.json")).toURI());
        final Path targetPath = Paths.get(getTargetDir(), "openapi-gen");
        final OpenApiClientGeneratorWrapper generatorWrapper = new OpenApiClientGeneratorWrapper(petstoreOpenApi, targetPath,
                getBuildTimeSpecPropertyPrefix(petstoreOpenApi));
        final List<File> generatedFiles = generatorWrapper.generate();
        assertNotNull(generatedFiles);
        assertFalse(generatedFiles.isEmpty());
    }

    private String getTargetDir() throws URISyntaxException {
        return Paths.get(requireNonNull(getClass().getResource("/")).toURI()).getParent().toString();
    }
}
