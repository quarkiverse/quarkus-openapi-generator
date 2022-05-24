package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderName;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import javax.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;

class BearerAuthenticationProviderTest extends AbstractAuthenticationProviderTest<BearerAuthenticationProvider> {

    private static final String TOKEN = "TOKEN";
    private static final String INCOMING_TOKEN = "INCOMING_TOKEN";

    private static final String BEARER_SCHEMA = "bearer";
    private static final String CUSTOM_SCHEMA = "custom_scheme";
    private static final String HEADER_NAME = "HEADER_NAME";

    @Override
    protected BearerAuthenticationProvider createProvider(String openApiSpecId, String authSchemeName,
            OpenApiGeneratorConfig openApiGeneratorConfig) {
        return new BearerAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, null, openApiGeneratorConfig);
    }

    @Test
    void filterNoSchemaCase() throws IOException {
        filter(null, TOKEN, TOKEN);
    }

    @Test
    void filterBearerSchemaCase() throws IOException {
        filter(BEARER_SCHEMA, TOKEN, "Bearer " + TOKEN);
    }

    @Test
    void filterCustomSchemaCase() throws IOException {
        filter(CUSTOM_SCHEMA, TOKEN, CUSTOM_SCHEMA + " " + TOKEN);
    }

    private void filter(String bearerScheme, String currentToken, String expectedAuthorizationHeader) throws IOException {
        provider = new BearerAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, bearerScheme, generatorConfig);
        authConfig.authConfigParams.put(BearerAuthenticationProvider.BEARER_TOKEN, currentToken);
        provider.filter(requestContext);
        assertHeader(headers, HttpHeaders.AUTHORIZATION, expectedAuthorizationHeader);
    }

    @ParameterizedTest
    @MethodSource("filterWithPropagationTestValues")
    void filterWithPropagation(String headerName,
            String currentToken,
            String bearerScheme,
            String expectedAuthorizationHeader) throws IOException {
        String propagatedHeaderName;
        if (headerName == null) {
            propagatedHeaderName = propagationHeaderName(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME,
                    HttpHeaders.AUTHORIZATION);
        } else {
            propagatedHeaderName = propagationHeaderName(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME,
                    HEADER_NAME);
        }
        headers.putSingle(propagatedHeaderName, INCOMING_TOKEN);
        authConfig.tokenPropagation = Optional.of(true);
        authConfig.headerName = Optional.ofNullable(headerName);
        filter(bearerScheme, currentToken, expectedAuthorizationHeader);
    }

    static Stream<Arguments> filterWithPropagationTestValues() {
        return Stream.of(
                Arguments.of(null, INCOMING_TOKEN, "bearer", "Bearer " + INCOMING_TOKEN),
                Arguments.of(null, INCOMING_TOKEN, CUSTOM_SCHEMA, CUSTOM_SCHEMA + " " + INCOMING_TOKEN),
                Arguments.of(HEADER_NAME, INCOMING_TOKEN, "bearer", "Bearer " + INCOMING_TOKEN),
                Arguments.of(HEADER_NAME, INCOMING_TOKEN, CUSTOM_SCHEMA, CUSTOM_SCHEMA + " " + INCOMING_TOKEN));
    }
}
