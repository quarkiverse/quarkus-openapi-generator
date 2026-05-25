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

import io.quarkiverse.openapi.generator.OpenApiSpec;
import io.quarkiverse.openapi.generator.markers.BasicAuthenticationMarker;
import io.quarkiverse.openapi.generator.oidc.providers.OAuth2AuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkiverse.openapi.generator.providers.BaseCompositeAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.BasicAuthenticationProvider;
import io.quarkus.test.QuarkusUnitTest;

public class BasicAuthWithoutOidcTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setFlatClassPath(true)
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(BasicAuthOnlyProvider.class)
                    .addAsResource(
                            new StringAsset("""
                                    quarkus.oidc-client.oauth_auth.auth-server-url=localhost
                                    quarkus.keycloak.devservices.enabled=false
                                    """),
                            "application.properties"));

    @Inject
    @OpenApiSpec(openApiSpecId = "spec_basic")
    BaseCompositeAuthenticationProvider compositeProvider;

    @Inject
    Instance<OAuth2AuthenticationProvider.OidcClientRequestFilterDelegate> oidcDelegateInstance;

    @Test
    public void shouldStartWithBasicAuthOnlyWithoutOidcDependency() {
        assertThat(compositeProvider.getAuthenticationProviders()).hasSize(1);
        AuthProvider authProvider = compositeProvider.getAuthenticationProviders().get(0);
        assertThat(authProvider).isInstanceOf(BasicAuthenticationProvider.class);
        assertThat(authProvider.getName()).isEqualTo("basic_auth");
    }

    @Test
    public void shouldNotRegisterOidcDelegateBeanWhenOnlyBasicAuthMarkersPresent() {
        assertThat(oidcDelegateInstance.isUnsatisfied()).isTrue();
    }

    @Priority(jakarta.ws.rs.Priorities.AUTHENTICATION)
    @BasicAuthenticationMarker(name = "basic_auth", openApiSpecId = "spec_basic")
    public static class BasicAuthOnlyProvider {
    }
}
