package io.quarkiverse.openapi.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.util.TypeLiteral;

import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkiverse.openapi.generator.providers.BaseCompositeAuthenticationProvider;
import io.quarkus.arc.SyntheticCreationalContext;

class AuthenticationRecorderTest {

    @SuppressWarnings("unchecked")
    @Test
    void skipsProvidersThatFailToInitializeWithoutFailingTheOthers() {
        AuthProvider workingProvider = mock(AuthProvider.class);

        Iterator<AuthProvider> brokenIterator = new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < 2;
            }

            @Override
            public AuthProvider next() {
                index++;
                if (index == 1) {
                    // Simulates a CreationException thrown while resolving a misconfigured provider,
                    // e.g. an OIDC-based one whose quarkus.oidc-client.* is not configured.
                    throw new RuntimeException("Unknown client");
                }
                return workingProvider;
            }
        };

        Instance<AuthProvider> authProviderInstances = mock(Instance.class);
        when(authProviderInstances.iterator()).thenReturn(brokenIterator);

        SyntheticCreationalContext<BaseCompositeAuthenticationProvider> ctx = mock(SyntheticCreationalContext.class);
        when(ctx.getInjectedReference(any(TypeLiteral.class), any())).thenReturn(authProviderInstances);

        AuthenticationRecorder recorder = new AuthenticationRecorder();
        Function<SyntheticCreationalContext<BaseCompositeAuthenticationProvider>, BaseCompositeAuthenticationProvider> factory = recorder
                .recordCompositeProvider("mySpec");

        BaseCompositeAuthenticationProvider compositeProvider = factory.apply(ctx);

        List<AuthProvider> resolvedProviders = compositeProvider.getAuthenticationProviders();
        assertEquals(1, resolvedProviders.size());
        assertSame(workingProvider, resolvedProviders.get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    void buildsCompositeProviderWithAllProvidersWhenNoneFail() {
        AuthProvider providerOne = mock(AuthProvider.class);
        AuthProvider providerTwo = mock(AuthProvider.class);

        Instance<AuthProvider> authProviderInstances = mock(Instance.class);
        when(authProviderInstances.iterator()).thenReturn(List.of(providerOne, providerTwo).iterator());

        SyntheticCreationalContext<BaseCompositeAuthenticationProvider> ctx = mock(SyntheticCreationalContext.class);
        when(ctx.getInjectedReference(any(TypeLiteral.class), any())).thenReturn(authProviderInstances);

        AuthenticationRecorder recorder = new AuthenticationRecorder();
        BaseCompositeAuthenticationProvider compositeProvider = recorder.recordCompositeProvider("mySpec").apply(ctx);

        assertEquals(List.of(providerOne, providerTwo), compositeProvider.getAuthenticationProviders());
    }

    @SuppressWarnings("unchecked")
    @Test
    void returnsEmptyCompositeProviderWhenEveryProviderFails() {
        Instance<AuthProvider> authProviderInstances = mock(Instance.class);
        Iterator<AuthProvider> alwaysFailingIterator = new Iterator<>() {
            private int remaining = 2;

            @Override
            public boolean hasNext() {
                return remaining > 0;
            }

            @Override
            public AuthProvider next() {
                if (remaining <= 0) {
                    throw new NoSuchElementException();
                }
                remaining--;
                throw new RuntimeException("Unknown client");
            }
        };
        when(authProviderInstances.iterator()).thenReturn(alwaysFailingIterator);

        SyntheticCreationalContext<BaseCompositeAuthenticationProvider> ctx = mock(SyntheticCreationalContext.class);
        when(ctx.getInjectedReference(any(TypeLiteral.class), any())).thenReturn(authProviderInstances);

        AuthenticationRecorder recorder = new AuthenticationRecorder();
        BaseCompositeAuthenticationProvider compositeProvider = recorder.recordCompositeProvider("mySpec").apply(ctx);

        assertEquals(List.of(), compositeProvider.getAuthenticationProviders());
    }
}
