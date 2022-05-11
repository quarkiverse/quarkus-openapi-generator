package io.quarkiverse.openapi.generator.providers;

import javax.ws.rs.core.MultivaluedMap;

import io.quarkiverse.openapi.generator.CodegenConfig;

public interface HeadersProvider {

    MultivaluedMap<String, String> getStringHeaders(CodegenConfig codegenConfig);

}
