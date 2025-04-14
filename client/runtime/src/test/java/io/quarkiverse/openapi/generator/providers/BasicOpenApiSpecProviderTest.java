package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderName;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.ws.rs.core.HttpHeaders;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import io.quarkiverse.openapi.generator.AuthConfig;

class BasicOpenApiSpecProviderTest extends AbstractOpenApiSpecProviderTest<BasicAuthenticationProvider> {

    private static final String PROPAGATED_TOKEN = "PROPAGATED_TOKEN";
    private static final String USER = "USER";
    private static final String PASSWORD = "PASSWORD";

    private static final String USER_PROP = "username";
    private static final String PASSWORD_PROP = "password";

    private static final String CUSTOM_SCHEMA = "custom_scheme";
    private static final String HEADER_NAME = "HEADER_NAME";

    private static final String EXPECTED_BASIC_TOKEN = "Basic "
            + Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes());

    @Override
    protected BasicAuthenticationProvider createProvider() {
        return new BasicAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, List.of());
    }

    @Test
    void filter() throws IOException {
        filter(EXPECTED_BASIC_TOKEN);
    }

    private void filter(String expectedAuthorizationHeader) throws IOException {
        provider.filter(requestContext);
        assertHeader(requestContext.getHeaders(), HttpHeaders.AUTHORIZATION, expectedAuthorizationHeader);
    }

    @ParameterizedTest
    @MethodSource("filterWithPropagationTestValues")
    void filterWithPropagation(String headerName,
            String expectedAuthorizationHeader) throws IOException {
        String propagatedHeaderName = headerName == null
                ? propagationHeaderName(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, HttpHeaders.AUTHORIZATION)
                : propagationHeaderName(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, HEADER_NAME);
        try (MockedStatic<ConfigProvider> configProviderMocked = Mockito.mockStatic(ConfigProvider.class)) {
            Config mockedConfig = Mockito.mock(Config.class);
            configProviderMocked.when(ConfigProvider::getConfig).thenReturn(mockedConfig);

            when(mockedConfig.getOptionalValue(provider.getCanonicalAuthConfigPropertyName(AuthConfig.TOKEN_PROPAGATION),
                    Boolean.class)).thenReturn(Optional.of(true));
            when(mockedConfig.getOptionalValue(provider.getCanonicalAuthConfigPropertyName(AuthConfig.HEADER_NAME),
                    String.class)).thenReturn(Optional.of(headerName == null ? HttpHeaders.AUTHORIZATION : headerName));
            when(mockedConfig.getOptionalValue(provider.getCanonicalAuthConfigPropertyName(USER_PROP),
                    String.class)).thenReturn(Optional.of(USER));
            when(mockedConfig.getOptionalValue(provider.getCanonicalAuthConfigPropertyName(PASSWORD_PROP),
                    String.class)).thenReturn(Optional.of(PASSWORD));
            headers.putSingle(propagatedHeaderName, PROPAGATED_TOKEN);
            filter(expectedAuthorizationHeader);
        }
    }

    static Stream<Arguments> filterWithPropagationTestValues() {
        return Stream.of(
                Arguments.of(null, "Basic " + PROPAGATED_TOKEN),
                Arguments.of(HEADER_NAME, "Basic " + PROPAGATED_TOKEN));
    }
}
