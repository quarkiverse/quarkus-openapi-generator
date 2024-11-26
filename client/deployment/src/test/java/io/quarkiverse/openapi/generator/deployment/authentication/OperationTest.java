package io.quarkiverse.openapi.generator.deployment.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.openapi.generator.OpenApiSpec;
import io.quarkiverse.openapi.generator.annotations.GeneratedClass;
import io.quarkiverse.openapi.generator.markers.BasicAuthenticationMarker;
import io.quarkiverse.openapi.generator.markers.OauthAuthenticationMarker;
import io.quarkiverse.openapi.generator.markers.OperationMarker;
import io.quarkiverse.openapi.generator.providers.CompositeAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.OperationAuthInfo;
import io.quarkus.test.QuarkusUnitTest;

public class OperationTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(PetApi.class)
                    .addClass(LocalAuthenticationProvider.class)
                    .addAsResource(
                            new StringAsset("""
                                    quarkus.oidc-client.oauth_auth.auth-server-url=localhost
                                    quarkus.keycloak.devservices.enabled=false
                                    """),
                            "application.properties"));

    @Inject
    @OpenApiSpec(openApiSpecId = "petstore_json")
    CompositeAuthenticationProvider compositeProvider;
    @Inject
    @OpenApiSpec(openApiSpecId = "other_spec_json")
    CompositeAuthenticationProvider otherProvider;

    @Test
    public void test() {
        assertThat(compositeProvider.getAuthenticationProviders()).hasSize(1);
        assertThat(compositeProvider.getAuthenticationProviders().get(0).operationsToFilter()).hasSize(1);
        assertThat(otherProvider.getAuthenticationProviders()).hasSize(1);
        assertThat(otherProvider.getAuthenticationProviders().get(0).operationsToFilter()).isEmpty();
        OperationAuthInfo operation = compositeProvider.getAuthenticationProviders().get(0).operationsToFilter().get(0);
        assertThat(operation.getOperationId()).isEqualTo("addPet");
        assertThat(operation.getHttpMethod()).isEqualTo("POST");
        assertThat(operation.getPath()).isEqualTo("/api/v3/method1");

    }

    @RegisterRestClient(baseUri = "http://localhost/api/v3", configKey = "petstore_json")
    @GeneratedClass(value = "petstore.json", tag = "Pet")
    @ApplicationScoped
    public interface PetApi {

        @OperationMarker(name = "oauth_auth", openApiSpecId = "petstore_json", operationId = "addPet", method = "POST", path = "/api/v3/method1")
        @Path("/method1")
        @POST
        Response method1();

    }

    @jakarta.annotation.Priority(jakarta.ws.rs.Priorities.AUTHENTICATION)
    @OauthAuthenticationMarker(name = "oauth_auth", openApiSpecId = "petstore_json")
    @BasicAuthenticationMarker(name = "basic_auth", openApiSpecId = "other_spec_json")
    public static class LocalAuthenticationProvider {

    }

}
