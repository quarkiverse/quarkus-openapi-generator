package io.quarkiverse.openapi.generator.providers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

/**
 * Composition of supported {@link ClientRequestFilter} defined by a given OpenAPI interface.
 * This class is used as the base class of generated code.
 */
public abstract class AbstractCompositeAuthenticationProvider implements ClientRequestFilter {

    private final List<ClientRequestFilter> authProviders = new ArrayList<>();

    public final void addAuthenticationProvider(final ClientRequestFilter authProvider) {
        this.authProviders.add(authProvider);
    }

    @Override
    public final void filter(ClientRequestContext requestContext) throws IOException {
        for (ClientRequestFilter authProvider : authProviders) {
            authProvider.filter(requestContext);
        }
    }

}
