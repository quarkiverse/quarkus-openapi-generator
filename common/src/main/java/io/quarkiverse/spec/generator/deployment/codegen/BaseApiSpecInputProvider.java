package io.quarkiverse.spec.generator.deployment.codegen;

import java.io.InputStream;
import java.util.List;

import io.quarkus.deployment.CodeGenContext;

/**
 * Provider interface for clients to dynamically provide their own XAPI specification files.
 */
public interface BaseApiSpecInputProvider<T extends BaseSpecInputModel> {

    /**
     * Fetch XAPI specification files from a given source.
     *
     * @return a list of spec files in {@link InputStream} format.
     */
    List<T> read(CodeGenContext context);

}
