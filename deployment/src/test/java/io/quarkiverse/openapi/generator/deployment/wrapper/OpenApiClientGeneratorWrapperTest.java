package io.quarkiverse.openapi.generator.deployment.wrapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

public class OpenApiClientGeneratorWrapperTest {

    @Test
    void generatePetStore() throws URISyntaxException {
        final String petstoreOpenApi = Objects.requireNonNull(this.getClass().getResource("/openapi/petstore-openapi.json"))
                .getPath();
        final String targetPath = Paths.get(Objects.requireNonNull(getClass().getResource("/")).toURI()).getParent().toString()
                + "/openapi-gen";
        final OpenApiClientGeneratorWrapper generatorWrapper = new OpenApiClientGeneratorWrapper(petstoreOpenApi, targetPath);
        final List<File> generatedFiles = generatorWrapper.generate();
        assertNotNull(generatedFiles);
        assertFalse(generatedFiles.isEmpty());
    }
}
