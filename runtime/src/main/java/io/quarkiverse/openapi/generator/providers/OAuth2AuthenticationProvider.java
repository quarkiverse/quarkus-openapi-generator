package io.quarkiverse.openapi.generator.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.quarkus.oidc.client.filter.OidcClientRequestFilter;

public class OAuth2AuthenticationProvider extends OidcClientRequestFilter implements AuthProvider {

    private final String clientId;
    private final List<OperationAuthInfo> applyToOperations = new ArrayList<>();

    public OAuth2AuthenticationProvider(final String clientId) {
        this.clientId = clientId;
        // it's fine calling it here since this class will be created on `init()` method of the generated CompositeAuthenticationProvider
        super.init();
    }

    @Override
    protected Optional<String> clientId() {
        return Optional.of(this.clientId);
    }

    @Override
    public String getName() {
        return this.clientId;
    }

    @Override
    public List<OperationAuthInfo> operationsToFilter() {
        return applyToOperations;
    }

    @Override
    public AuthProvider addOperation(OperationAuthInfo operationAuthInfo) {
        this.applyToOperations.add(operationAuthInfo);
        return this;
    }
}
