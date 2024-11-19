package io.quarkiverse.openapi.generator;

import java.io.IOException;
import java.util.function.Consumer;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestFilter;

import io.quarkiverse.openapi.generator.providers.OAuth2AuthenticationProvider;
import io.quarkus.logging.Log;
import io.quarkus.oidc.client.Tokens;
import io.quarkus.oidc.client.runtime.AbstractTokensProducer;
import io.quarkus.oidc.client.runtime.DisabledOidcClientException;
import io.quarkus.oidc.common.runtime.OidcConstants;

@Priority(Priorities.AUTHENTICATION)
@OidcClient
public class ReactiveOidcClientRequestFilterDelegate extends AbstractTokensProducer
        implements ResteasyReactiveClientRequestFilter, OAuth2AuthenticationProvider.OidcClientRequestFilterDelegate {

    private static final String BEARER_SCHEME_WITH_SPACE = OidcConstants.BEARER_SCHEME + " ";

    final String clientId;

    ReactiveOidcClientRequestFilterDelegate(InjectionPoint injectionPoint) {
        OidcClient annotation = (OidcClient) injectionPoint.getQualifiers().stream()
                .filter(x -> x.annotationType().equals(OidcClient.class))
                .findFirst().orElseThrow();
        this.clientId = OpenApiGeneratorConfig.getSanitizedSecuritySchemeName(annotation.name());
    }

    @Override
    protected java.util.Optional<String> clientId() {
        return java.util.Optional.of(clientId);
    }

    @Override
    protected void initTokens() {
        if (earlyTokenAcquisition) {
            Log.debug("Token acquisition will be delayed until this filter is executed to avoid blocking an IO thread");
        }
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        filter((ResteasyReactiveClientRequestContext) requestContext);
    }

    @Override
    public void filter(ResteasyReactiveClientRequestContext requestContext) {
        requestContext.suspend();

        super.getTokens().subscribe().with(new Consumer<>() {
            @Override
            public void accept(Tokens tokens) {
                requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION,
                        BEARER_SCHEME_WITH_SPACE + tokens.getAccessToken());
                requestContext.resume();
            }
        }, new Consumer<>() {
            @Override
            public void accept(Throwable t) {
                if (t instanceof DisabledOidcClientException) {
                    Log.debug("Client is disabled, acquiring and propagating the token is not necessary");
                    requestContext.resume();
                } else {
                    Log.debugf("Access token is not available, cause: %s, aborting the request", t.getMessage());
                    requestContext.resume((t instanceof RuntimeException) ? t : new RuntimeException(t));
                }
            }
        });
    }
}
