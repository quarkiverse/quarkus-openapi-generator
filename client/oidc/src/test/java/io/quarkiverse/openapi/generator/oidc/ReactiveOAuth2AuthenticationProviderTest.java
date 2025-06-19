package io.quarkiverse.openapi.generator.oidc;

import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderName;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.assertj.core.api.Assertions;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.resteasy.reactive.client.impl.ClientRequestContextImpl;
import org.jboss.resteasy.reactive.client.impl.RestClientRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkiverse.openapi.generator.AuthConfig;
import io.quarkiverse.openapi.generator.oidc.providers.OAuth2AuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;
import io.quarkus.oidc.client.Tokens;
import io.smallrye.mutiny.Uni;

@ExtendWith(MockitoExtension.class)
public class ReactiveOAuth2AuthenticationProviderTest {
    private static final String OPEN_API_FILE_SPEC_ID = "open_api_file_spec_id_json";
    private static final String AUTH_SCHEME_NAME = "auth_scheme_name";

    private static final String ACCESS_TOKEN = "REACTIVE_ACCESS_TOKEN";

    private static final String PROPAGATED_TOKEN = "PROPAGATED_TOKEN";

    private static final String HEADER_NAME = "HEADER_NAME";
    @Mock
    private ClientRequestContextImpl reactiveRequestContext;

    @Mock
    private RestClientRequestContext restClientRequestContext;
    private MultivaluedMap<String, Object> headers;

    private ReactiveOidcClientRequestFilterDelegate reactiveDelegate;
    private static final Tokens token = new Tokens(ACCESS_TOKEN, Long.MAX_VALUE, null, "", Long.MAX_VALUE, null, "");
    private static final Uni<Tokens> uniToken = Uni.createFrom().item(token);

    private OAuth2AuthenticationProvider provider;

    @BeforeEach
    void setUp() {
        headers = new MultivaluedHashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList("TEST"));
        Mockito.lenient().doReturn(headers).when(reactiveRequestContext).getHeaders();
        Mockito.lenient().doReturn(restClientRequestContext).when(reactiveRequestContext).getRestClientRequestContext();
        Mockito.lenient().doAnswer(invocationOnMock -> restClientRequestContext.setSuspended(true))
                .when(restClientRequestContext).suspend();
        Mockito.lenient().doAnswer(invocationOnMock -> restClientRequestContext.setSuspended(false))
                .when(restClientRequestContext).resume();
        reactiveDelegate = Mockito.mock(ReactiveOidcClientRequestFilterDelegate.class);
        try {
            Mockito.lenient().doCallRealMethod().when(reactiveDelegate).filter(Mockito.any(ClientRequestContext.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Mockito.lenient().doCallRealMethod().when(reactiveDelegate).filter(reactiveRequestContext);
        Mockito.lenient().when(reactiveDelegate.getTokens()).thenReturn(uniToken);

        provider = createReactiveProvider();
    }

    protected OAuth2AuthenticationProvider createReactiveProvider() {
        return new OAuth2AuthenticationProvider(AUTH_SCHEME_NAME, OPEN_API_FILE_SPEC_ID, reactiveDelegate, List.of(),
                new ConfigCredentialsProvider());
    }

    protected void assertHeader(MultivaluedMap<String, Object> headers, String headerName, String value) {
        Assertions.assertThat(headers.getFirst(headerName))
                .isNotNull()
                .isEqualTo(value);
    }

    static Stream<Arguments> filterWithPropagationTestValues() {
        return Stream.of(
                Arguments.of(null, "Bearer " + PROPAGATED_TOKEN),
                Arguments.of(HEADER_NAME, "Bearer " + PROPAGATED_TOKEN));
    }

    @Test
    void filterReactive() throws IOException, InterruptedException {
        filter(provider, "Bearer " + ACCESS_TOKEN);
    }

    private void filter(OAuth2AuthenticationProvider provider, String expectedAuthorizationHeader)
            throws IOException, InterruptedException {
        provider.filter(reactiveRequestContext);
        while (reactiveRequestContext.getRestClientRequestContext().isSuspended()) {
            Thread.sleep(1000);
        }
        assertHeader(headers, HttpHeaders.AUTHORIZATION, expectedAuthorizationHeader);
    }

    @ParameterizedTest
    @MethodSource("filterWithPropagationTestValues")
    void filterWithPropagation(String headerName,
            String expectedAuthorizationHeader) throws IOException, InterruptedException {
        String propagatedHeaderName;
        if (headerName == null) {
            propagatedHeaderName = propagationHeaderName(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME,
                    HttpHeaders.AUTHORIZATION);
        } else {
            propagatedHeaderName = propagationHeaderName(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME,
                    HEADER_NAME);
        }
        try (MockedStatic<ConfigProvider> configProviderMocked = Mockito.mockStatic(ConfigProvider.class)) {
            Config mockedConfig = Mockito.mock(Config.class);
            configProviderMocked.when(ConfigProvider::getConfig).thenReturn(mockedConfig);

            when(mockedConfig.getOptionalValue(provider.getCanonicalAuthConfigPropertyName(AuthConfig.TOKEN_PROPAGATION),
                    Boolean.class)).thenReturn(Optional.of(true));
            when(mockedConfig.getOptionalValue(provider.getCanonicalAuthConfigPropertyName(AuthConfig.HEADER_NAME),
                    String.class)).thenReturn(Optional.of(headerName == null ? HttpHeaders.AUTHORIZATION : headerName));

            headers.putSingle(propagatedHeaderName, PROPAGATED_TOKEN);
            filter(provider, expectedAuthorizationHeader);
        }
    }
}
