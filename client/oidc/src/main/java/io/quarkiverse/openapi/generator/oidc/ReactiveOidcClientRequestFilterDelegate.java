package io.quarkiverse.openapi.generator.oidc;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestFilter;

import io.quarkiverse.openapi.generator.OidcClient;
import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkiverse.openapi.generator.oidc.providers.OAuth2AuthenticationProvider;
import io.quarkus.oidc.client.runtime.AbstractTokensProducer;
import io.quarkus.oidc.client.runtime.DisabledOidcClientException;
import io.quarkus.oidc.common.runtime.OidcConstants;

@Priority(Priorities.AUTHENTICATION)
@OidcClient
public class ReactiveOidcClientRequestFilterDelegate extends AbstractTokensProducer
        implements ResteasyReactiveClientRequestFilter, OAuth2AuthenticationProvider.OidcClientRequestFilterDelegate {

    private static final Logger LOG = Logger.getLogger(ReactiveOidcClientRequestFilterDelegate.class);
    private static final String BEARER_SCHEME_WITH_SPACE = OidcConstants.BEARER_SCHEME + " ";

    final String clientId;

    ReactiveOidcClientRequestFilterDelegate(InjectionPoint injectionPoint) {
        OidcClient annotation = (OidcClient) injectionPoint.getQualifiers().stream()
                .filter(x -> x.annotationType().equals(OidcClient.class)).findFirst().orElseThrow();
        this.clientId = OpenApiGeneratorConfig.getSanitizedSecuritySchemeName(annotation.name());
    }

    @Override
    protected java.util.Optional<String> clientId() {
        return java.util.Optional.of(clientId);
    }

    @Override
    protected void initTokens() {
        if (earlyTokenAcquisition) {
            LOG.debug("Token acquisition will be delayed until this filter is executed to avoid blocking an IO thread");
        }
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        filter((ResteasyReactiveClientRequestContext) requestContext);
    }

    @Override
    public void filter(ResteasyReactiveClientRequestContext requestContext) {
        requestContext.suspend();

        super.getTokens().subscribe().with(tokens -> {
            requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION,
                    BEARER_SCHEME_WITH_SPACE + tokens.getAccessToken());
            requestContext.resume();
        }, t -> {
            if (t instanceof DisabledOidcClientException) {
                LOG.debug("Client is disabled, acquiring and propagating the token is not necessary");
                requestContext.resume();
            } else {
                LOG.debugf("Access token is not available, cause: %s, aborting the request", t.getMessage());
                requestContext.resume((t instanceof RuntimeException) ? t : new RuntimeException(t));
            }
        });
    }
}
