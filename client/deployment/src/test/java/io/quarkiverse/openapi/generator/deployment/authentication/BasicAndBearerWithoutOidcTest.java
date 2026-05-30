package io.quarkiverse.openapi.generator.deployment.authentication;

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
import io.quarkiverse.openapi.generator.markers.BasicAuthenticationMarker;
import io.quarkiverse.openapi.generator.markers.BearerAuthenticationMarker;
import io.quarkiverse.openapi.generator.providers.BaseCompositeAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.BasicAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.BearerAuthenticationProvider;
import io.quarkus.test.QuarkusUnitTest;

@DisabledOnOs(OS.WINDOWS)
public class BasicAndBearerWithoutOidcTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setFlatClassPath(true)
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(MultiAuthProvider.class)
                    .addAsResource(
                            new StringAsset(
                                    """
                                                    quarkus.openapi-generator.auth.basic_auth.auth.username=test
                                                    quarkus.openapi-generator.auth.basic_auth.auth.password=test
                                                    quarkus.openapi-generator.auth.bearer_auth.auth.bearer-token=test
                                                    quarkus.keycloak.devservices.enabled=false
                                            """),
                            "application.properties"));

    @Inject
    @OpenApiSpec(openApiSpecId = "spec_mixed")
    BaseCompositeAuthenticationProvider compositeProvider;

    @Test
    public void shouldStartWithBasicAndBearerWithoutOidcDependency() {
        assertThat(compositeProvider.getAuthenticationProviders()).hasSize(2);
        assertThat(compositeProvider.getAuthenticationProviders())
                .anyMatch(authProvider -> authProvider instanceof BasicAuthenticationProvider
                        && authProvider.getName().equals("basic_auth"));
        assertThat(compositeProvider.getAuthenticationProviders())
                .anyMatch(authProvider -> authProvider instanceof BearerAuthenticationProvider
                        && authProvider.getName().equals("bearer_auth"));
    }

    @Priority(jakarta.ws.rs.Priorities.AUTHENTICATION)
    @BasicAuthenticationMarker(name = "basic_auth", openApiSpecId = "spec_mixed")
    @BearerAuthenticationMarker(name = "bearer_auth", openApiSpecId = "spec_mixed", scheme = "bearer")
    public static class MultiAuthProvider {
    }
}
