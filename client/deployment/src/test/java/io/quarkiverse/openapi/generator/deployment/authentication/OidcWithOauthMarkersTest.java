package io.quarkiverse.openapi.generator.deployment.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openapi.generator.OidcClient;
import io.quarkiverse.openapi.generator.OpenApiSpec;
import io.quarkiverse.openapi.generator.markers.OauthAuthenticationMarker;
import io.quarkiverse.openapi.generator.oidc.providers.OAuth2AuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkiverse.openapi.generator.providers.BaseCompositeAuthenticationProvider;
import io.quarkus.test.QuarkusUnitTest;

public class OidcWithOauthMarkersTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setFlatClassPath(true)
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(OauthProvider.class)
                    .addAsResource(
                            new StringAsset("""
                                    quarkus.oidc-client.oauth_auth.auth-server-url=localhost
                                    quarkus.keycloak.devservices.enabled=false
                                    """),
                            "application.properties"));

    @Inject
    @OpenApiSpec(openApiSpecId = "spec_oauth")
    BaseCompositeAuthenticationProvider compositeProvider;

    @Inject
    @OidcClient(name = "oauth_auth")
    Instance<OAuth2AuthenticationProvider.OidcClientRequestFilterDelegate> oidcDelegateInstance;

    @Test
    public void shouldRegisterOidcDelegateBeanWhenOauthMarkersPresent() {
        assertThat(oidcDelegateInstance.isUnsatisfied()).isFalse();
        assertThat(oidcDelegateInstance.get()).isNotNull();
    }

    @Test
    public void shouldRegisterOauth2ProviderWhenOauthMarkersAndOidcPresent() {
        assertThat(compositeProvider.getAuthenticationProviders()).hasSize(1);
        AuthProvider authProvider = compositeProvider.getAuthenticationProviders().get(0);
        assertThat(authProvider).isInstanceOf(OAuth2AuthenticationProvider.class);
        assertThat(authProvider.getName()).isEqualTo("oauth_auth");
    }

    @Priority(jakarta.ws.rs.Priorities.AUTHENTICATION)
    @OauthAuthenticationMarker(name = "oauth_auth", openApiSpecId = "spec_oauth")
    public static class OauthProvider {
    }
}
