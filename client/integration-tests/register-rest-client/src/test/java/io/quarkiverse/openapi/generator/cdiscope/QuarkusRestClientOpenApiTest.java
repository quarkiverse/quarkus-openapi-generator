package io.quarkiverse.openapi.generator.cdiscope;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class QuarkusRestClientOpenApiTest {
    private static final Path ROOT_GEN_SOURCES_PACKAGE = Path.of("target", "generated-sources", "open-api", "com", "oapi",
            "pkg");

    @Inject
    @RestClient
    com.oapi.pkg.rest_client_true.api.HelloResourceApi restClientTrueApi;

    @Inject
    @RestClient
    com.oapi.pkg.rest_client_unspecified.api.HelloResourceApi restClientUnspecifiedApi;

    private static com.oapi.pkg.rest_client_false.api.HelloResourceApi getRestClientFalseApi() {
        return RestClientBuilder.newBuilder()
                .baseUri("http://localhost:8080")
                .build(com.oapi.pkg.rest_client_false.api.HelloResourceApi.class);
    }

    @Test
    void restClientTrueApiGeneratedCorrectly() {
        assertThat(this.restClientTrueApi).isNotNull();
        assertThat(ROOT_GEN_SOURCES_PACKAGE.resolve(Path.of("rest_client_true", "api", "HelloResourceApi.java")))
                .isReadable()
                .content()
                .contains(RegisterRestClient.class.getName());
    }

    @Test
    void restClientUnspecifiedApiGeneratedCorrectly() {
        assertThat(this.restClientUnspecifiedApi).isNotNull();
        assertThat(ROOT_GEN_SOURCES_PACKAGE.resolve(Path.of("rest_client_unspecified", "api", "HelloResourceApi.java")))
                .isReadable()
                .content()
                .contains(RegisterRestClient.class.getName());
    }

    @Test
    void restClientFalseApiGeneratedCorrectly() {
        var restClientFalseApi = getRestClientFalseApi();
        assertThat(restClientFalseApi).isNotNull();
        assertThat(ROOT_GEN_SOURCES_PACKAGE.resolve(Path.of("rest_client_false", "api", "HelloResourceApi.java")))
                .isReadable()
                .content()
                .doesNotContain(RegisterRestClient.class.getName());
    }
}
