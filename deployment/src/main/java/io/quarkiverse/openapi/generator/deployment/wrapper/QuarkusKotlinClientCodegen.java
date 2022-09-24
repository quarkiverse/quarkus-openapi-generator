package io.quarkiverse.openapi.generator.deployment.wrapper;

import static org.openapitools.codegen.languages.JavaClientCodegen.SERIALIZATION_LIBRARY_JACKSON;

import org.openapitools.codegen.languages.KotlinClientCodegen;

public class QuarkusKotlinClientCodegen extends KotlinClientCodegen {

    public QuarkusKotlinClientCodegen() {
        this.setSerializationLibrary(SERIALIZATION_LIBRARY_JACKSON);
        this.setTemplateDir("templates/kotlin");
    }

    @Override
    public String getName() {
        return "quarkus-kotlin";
    }
}
