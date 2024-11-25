package io.quarkiverse.openapi.generator.oidc;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import org.jboss.logging.Logger;

import io.quarkiverse.openapi.generator.OidcClient;
import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkiverse.openapi.generator.oidc.providers.OAuth2AuthenticationProvider;
import io.quarkus.oidc.client.runtime.AbstractTokensProducer;
import io.quarkus.oidc.client.runtime.DisabledOidcClientException;

@Priority(Priorities.AUTHENTICATION)
@OidcClient
public class ClassicOidcClientRequestFilterDelegate extends AbstractTokensProducer
        implements ClientRequestFilter, OAuth2AuthenticationProvider.OidcClientRequestFilterDelegate {

    private static final Logger LOG = Logger.getLogger(ClassicOidcClientRequestFilterDelegate.class);

    final String clientId;

    ClassicOidcClientRequestFilterDelegate(InjectionPoint injectionPoint) {
        OidcClient annotation = (OidcClient) injectionPoint.getQualifiers().stream()
                .filter(x -> x.annotationType().equals(OidcClient.class)).findFirst().orElseThrow();

        this.clientId = OpenApiGeneratorConfig.getSanitizedSecuritySchemeName(annotation.name());
    }

    @Override
    protected java.util.Optional<String> clientId() {
        return java.util.Optional.of(clientId);
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        try {
            String accessToken = this.getAccessToken();
            requestContext.getHeaders().add("Authorization", "Bearer " + accessToken);
        } catch (DisabledOidcClientException ex) {
            LOG.debug("Client is disabled, acquiring and propagating the token is not necessary");
        } catch (RuntimeException ex) {
            LOG.debugf("Access token is not available, cause: %s, aborting the request", ex.getMessage());
            throw ex;
        }
    }

    private String getAccessToken() {
        return this.awaitTokens().getAccessToken();
    }
}
