package io.quarkiverse.openapi.generator.deployment.authentication;

import static io.quarkiverse.openapi.generator.providers.ApiKeyIn.header;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openapi.generator.OpenApiSpec;
import io.quarkiverse.openapi.generator.markers.ApiKeyAuthenticationMarker;
import io.quarkiverse.openapi.generator.markers.BasicAuthenticationMarker;
import io.quarkiverse.openapi.generator.markers.OauthAuthenticationMarker;
import io.quarkiverse.openapi.generator.oidc.providers.OAuth2AuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.ApiKeyAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkiverse.openapi.generator.providers.BasicAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.CompositeAuthenticationProvider;
import io.quarkus.test.QuarkusUnitTest;

public class OpenApiSpecProviderTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(LocalAuthenticationProvider.class)
                    .addAsResource(
                            new StringAsset("""
                                    quarkus.oidc-client.oauth_auth.auth-server-url=localhost
                                    quarkus.oidc-client.oauth_auth1.auth-server-url=localhost
                                    quarkus.oidc-client.oauth_auth2.auth-server-url=localhost
                                    quarkus.keycloak.devservices.enabled=false
                                    """),
                            "application.properties"));

    @Inject
    @OpenApiSpec(openApiSpecId = "spec_1")
    CompositeAuthenticationProvider spec1CompositeProvider;

    @Inject
    @OpenApiSpec(openApiSpecId = "spec_2")
    CompositeAuthenticationProvider spec2CompositeProvider;

    @Inject
    @OpenApiSpec(openApiSpecId = "spec_3")
    CompositeAuthenticationProvider spec3CompositeProvider;

    @Inject
    @OpenApiSpec(openApiSpecId = "spec_multi")
    CompositeAuthenticationProvider multiCompositeProvider;

    @Test
    public void checkCompositeProvider() {
        assertThat(spec1CompositeProvider.getAuthenticationProviders()).hasSize(1);
        assertThat(spec2CompositeProvider.getAuthenticationProviders()).hasSize(1);
        assertThat(spec3CompositeProvider.getAuthenticationProviders()).hasSize(1);
        AuthProvider authProvider = spec1CompositeProvider.getAuthenticationProviders().get(0);
        assertThat(authProvider).isInstanceOf(OAuth2AuthenticationProvider.class);
        assertThat(authProvider.getName()).isEqualTo("oauth_auth");
        assertThat(((OAuth2AuthenticationProvider) authProvider).getOpenApiSpecId()).isEqualTo("spec_1");
        authProvider = spec2CompositeProvider.getAuthenticationProviders().get(0);
        assertThat(authProvider).isInstanceOf(BasicAuthenticationProvider.class);
        assertThat(authProvider.getName()).isEqualTo("basic_auth");
        assertThat(((BasicAuthenticationProvider) authProvider).getOpenApiSpecId()).isEqualTo("spec_2");
        authProvider = spec3CompositeProvider.getAuthenticationProviders().get(0);
        assertThat(authProvider).isInstanceOf(ApiKeyAuthenticationProvider.class);
        assertThat(authProvider.getName()).isEqualTo("api_key");
        assertThat(((ApiKeyAuthenticationProvider) authProvider).getOpenApiSpecId()).isEqualTo("spec_3");
    }

    @Test
    public void checkCompositeProviderWithMultipleAuth() {
        Assertions.assertEquals(4, multiCompositeProvider.getAuthenticationProviders().size());
    }

    @Priority(jakarta.ws.rs.Priorities.AUTHENTICATION)
    @OauthAuthenticationMarker(name = "oauth_auth", openApiSpecId = "spec_1")
    @BasicAuthenticationMarker(name = "basic_auth", openApiSpecId = "spec_2")
    @ApiKeyAuthenticationMarker(name = "api_key", openApiSpecId = "spec_3", apiKeyIn = header, apiKeyName = "api_key")
    @OauthAuthenticationMarker(name = "oauth_auth1", openApiSpecId = "spec_multi")
    @OauthAuthenticationMarker(name = "oauth_auth2", openApiSpecId = "spec_multi")
    @BasicAuthenticationMarker(name = "basic_auth1", openApiSpecId = "spec_multi")
    @BasicAuthenticationMarker(name = "basic_auth2", openApiSpecId = "spec_multi")
    public static class LocalAuthenticationProvider {

    }

}
