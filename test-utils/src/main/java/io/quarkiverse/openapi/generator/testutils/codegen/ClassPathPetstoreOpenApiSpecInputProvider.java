package io.quarkiverse.openapi.generator.testutils.codegen;

import java.util.Collections;
import java.util.List;

import io.quarkiverse.openapi.generator.codegen.OpenApiSpecInputProvider;
import io.quarkiverse.openapi.generator.codegen.SpecInputModel;

/**
 * Class used during tests to read the spec PetStore file from an alternative input.
 * The Petstore spec file must be named petstore.json in the src/main/resources/specs directory.
 * In a real production environment, implementations should dynamically load these spec files.
 */
public class ClassPathPetstoreOpenApiSpecInputProvider implements OpenApiSpecInputProvider {

    @Override
    public List<SpecInputModel> read() {
        return Collections.singletonList(
                new SpecInputModel("petstore.json", this.getClass().getResourceAsStream("/specs/petstore.json")));
    }

}
