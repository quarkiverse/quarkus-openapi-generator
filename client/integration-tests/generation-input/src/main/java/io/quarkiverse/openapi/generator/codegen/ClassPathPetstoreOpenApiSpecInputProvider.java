package io.quarkiverse.openapi.generator.codegen;

import java.util.ArrayList;
import java.util.List;

import io.quarkiverse.openapi.generator.deployment.codegen.OpenApiSpecInputProvider;
import io.quarkiverse.openapi.generator.deployment.codegen.SpecInputModel;
import io.quarkus.deployment.CodeGenContext;

/**
 * Class used during tests to read the spec PetStore file from an alternative input.
 * The Petstore spec file must be named petstore.json in the src/main/resources/specs directory.
 * In a real production environment, implementations should dynamically load these spec files.
 */
public class ClassPathPetstoreOpenApiSpecInputProvider implements OpenApiSpecInputProvider {

    @Override
    public List<SpecInputModel> read(CodeGenContext context) {
        final List<SpecInputModel> inputModels = new ArrayList<>();
        inputModels.add(new SpecInputModel("petstore.json", this.getClass().getResourceAsStream("/specs/petstore.json")));
        inputModels.add(new SpecInputModel("subtraction.yaml", this.getClass().getResourceAsStream("/specs/subtraction.yaml"),
                "org.math"));
        return inputModels;
    }

}
