package io.quarkiverse.openapi.generator.deployment.authentication;

import static io.quarkiverse.openapi.generator.providers.ApiKeyIn.header;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openapi.generator.OpenApiSpec;
import io.quarkiverse.openapi.generator.markers.ApiKeyAuthenticationMarker;
import io.quarkiverse.openapi.generator.providers.ApiKeyAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkiverse.openapi.generator.providers.BaseCompositeAuthenticationProvider;
import io.quarkus.test.QuarkusUnitTest;

@DisabledOnOs(OS.WINDOWS)
public class NoOidcWithoutOauthMarkersTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setFlatClassPath(true)
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(ApiKeyOnlyProvider.class)
                    .addAsResource(
                            new StringAsset("quarkus.keycloak.devservices.enabled=false\n"),
                            "application.properties"));

    @Inject
    @OpenApiSpec(openApiSpecId = "spec_apikey")
    BaseCompositeAuthenticationProvider compositeProvider;

    @Test
    public void shouldStartWithoutOidcWhenNoOauthMarkersPresent() {
        assertThat(compositeProvider.getAuthenticationProviders()).hasSize(1);
        AuthProvider authProvider = compositeProvider.getAuthenticationProviders().get(0);
        assertThat(authProvider).isInstanceOf(ApiKeyAuthenticationProvider.class);
        assertThat(authProvider.getName()).isEqualTo("api_key");
    }

    @Priority(jakarta.ws.rs.Priorities.AUTHENTICATION)
    @ApiKeyAuthenticationMarker(name = "api_key", openApiSpecId = "spec_apikey", apiKeyIn = header, apiKeyName = "X-API-Key")
    public static class ApiKeyOnlyProvider {
    }
}
