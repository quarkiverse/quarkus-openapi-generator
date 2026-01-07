package io.quarkiverse.openapi.generator.cdiscope;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class QuarkusApiCdiScopeOpenApiTests {
    private static final Path ROOT_GEN_SOURCES_PACKAGE = Path.of("target", "generated-sources", "open-api", "com", "oapi",
            "pkg");

    @Inject
    @RestClient
    com.oapi.pkg.cdi_scope_singleton.api.HelloResourceApi singletonApi;

    @Inject
    @RestClient
    com.oapi.pkg.cdi_scope_unspecified.api.HelloResourceApi unspecifiedApi;

    @Test
    void singletonApiGeneratedCorrectly() {
        assertThat(this.singletonApi).isNotNull();

        assertThat(ROOT_GEN_SOURCES_PACKAGE.resolve(Path.of("cdi_scope_singleton", "api", "HelloResourceApi.java")))
                .isReadable()
                .content()
                .contains(Singleton.class.getName());
    }

    @Test
    void unspecifiedApiGeneratedCorrectly() {
        assertThat(this.unspecifiedApi).isNotNull();

        assertThat(ROOT_GEN_SOURCES_PACKAGE.resolve(Path.of("cdi_scope_unspecified", "api", "HelloResourceApi.java")))
                .isReadable()
                .content()
                .contains(ApplicationScoped.class.getName());
    }
}
