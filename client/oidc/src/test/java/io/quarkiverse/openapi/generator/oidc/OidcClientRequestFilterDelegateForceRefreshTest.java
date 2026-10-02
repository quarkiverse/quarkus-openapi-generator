package io.quarkiverse.openapi.generator.oidc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;

import jakarta.enterprise.inject.spi.InjectionPoint;

import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.OidcClient;

class OidcClientRequestFilterDelegateForceRefreshTest {

    // Each test uses its own securityScheme name since the force-refresh flag is tracked in a registry
    // shared across delegate instances for the same clientId (see ForceTokenRefreshRegistry), to keep
    // tests independent from one another.
    private static InjectionPoint newInjectionPointFor(String schemeName) {
        InjectionPoint injectionPoint = mock(InjectionPoint.class);
        when(injectionPoint.getQualifiers()).thenReturn(Set.of(new OidcClient.Literal(schemeName)));
        return injectionPoint;
    }

    private static String uniqueSchemeName() {
        return "scheme_" + UUID.randomUUID().toString().replace("-", "");
    }

    @Test
    void reactiveDelegateDoesNotForceNewTokensByDefault() {
        ReactiveOidcClientRequestFilterDelegate delegate = new ReactiveOidcClientRequestFilterDelegate(
                newInjectionPointFor(uniqueSchemeName()));

        assertThat(delegate.isForceNewTokens()).isFalse();
    }

    @Test
    void reactiveDelegateForcesNewTokensExactlyOnceAfterRequested() {
        ReactiveOidcClientRequestFilterDelegate delegate = new ReactiveOidcClientRequestFilterDelegate(
                newInjectionPointFor(uniqueSchemeName()));

        delegate.forceTokenRefresh();

        assertThat(delegate.isForceNewTokens()).isTrue();
        assertThat(delegate.isForceNewTokens()).isFalse();
    }

    @Test
    void reactiveDelegatesForSameClientIdShareTheForceRefreshFlag() {
        String schemeName = uniqueSchemeName();
        ReactiveOidcClientRequestFilterDelegate requestSideDelegate = new ReactiveOidcClientRequestFilterDelegate(
                newInjectionPointFor(schemeName));
        ReactiveOidcClientRequestFilterDelegate responseSideDelegate = new ReactiveOidcClientRequestFilterDelegate(
                newInjectionPointFor(schemeName));

        responseSideDelegate.forceTokenRefresh();

        assertThat(requestSideDelegate.isForceNewTokens()).isTrue();
        assertThat(requestSideDelegate.isForceNewTokens()).isFalse();
    }

    @Test
    void classicDelegateDoesNotForceNewTokensByDefault() {
        ClassicOidcClientRequestFilterDelegate delegate = new ClassicOidcClientRequestFilterDelegate(
                newInjectionPointFor(uniqueSchemeName()));

        assertThat(delegate.isForceNewTokens()).isFalse();
    }

    @Test
    void classicDelegateForcesNewTokensExactlyOnceAfterRequested() {
        ClassicOidcClientRequestFilterDelegate delegate = new ClassicOidcClientRequestFilterDelegate(
                newInjectionPointFor(uniqueSchemeName()));

        delegate.forceTokenRefresh();

        assertThat(delegate.isForceNewTokens()).isTrue();
        assertThat(delegate.isForceNewTokens()).isFalse();
    }

    @Test
    void classicDelegatesForSameClientIdShareTheForceRefreshFlag() {
        String schemeName = uniqueSchemeName();
        ClassicOidcClientRequestFilterDelegate requestSideDelegate = new ClassicOidcClientRequestFilterDelegate(
                newInjectionPointFor(schemeName));
        ClassicOidcClientRequestFilterDelegate responseSideDelegate = new ClassicOidcClientRequestFilterDelegate(
                newInjectionPointFor(schemeName));

        responseSideDelegate.forceTokenRefresh();

        assertThat(requestSideDelegate.isForceNewTokens()).isTrue();
        assertThat(requestSideDelegate.isForceNewTokens()).isFalse();
    }
}
