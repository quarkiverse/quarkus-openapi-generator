package io.quarkiverse.openapi.generator.providers;

import static io.quarkiverse.openapi.generator.providers.AbstractAuthenticationPropagationHeadersFactory.propagationHeaderName;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BearerOpenApiSpecProviderTest extends AbstractOpenApiSpecProviderTest<BearerAuthenticationProvider> {

    private static final String INCOMING_TOKEN = "INCOMING_TOKEN";

    private static final String BEARER_SCHEMA = "bearer";
    private static final String CUSTOM_SCHEMA = "custom_scheme";
    private static final String HEADER_NAME = "HEADER_NAME";

    @Override
    protected BearerAuthenticationProvider createProvider() {
        return new BearerAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, null,
                List.of());
    }

    @Test
    void filterNoSchemaCase() throws IOException {
        filter(null, INCOMING_TOKEN);
    }

    @Test
    void filterBearerSchemaCase() throws IOException {
        filter(BEARER_SCHEMA, "Bearer " + INCOMING_TOKEN);
    }

    @Test
    void filterCustomSchemaCase() throws IOException {
        filter(CUSTOM_SCHEMA, CUSTOM_SCHEMA + " " + INCOMING_TOKEN);
    }

    private void filter(String bearerScheme, String expectedAuthorizationHeader) throws IOException {
        provider = new BearerAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, bearerScheme,
                List.of());
        provider.filter(requestContext);
        assertHeader(headers, HttpHeaders.AUTHORIZATION, expectedAuthorizationHeader);
    }

    @ParameterizedTest
    @MethodSource("filterWithPropagationTestValues")
    void filterWithPropagation(String headerName,
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
        filter(bearerScheme, expectedAuthorizationHeader);
    }

    static Stream<Arguments> filterWithPropagationTestValues() {
        return Stream.of(
                Arguments.of(null, "bearer", "Bearer " + INCOMING_TOKEN),
                Arguments.of(null, CUSTOM_SCHEMA, CUSTOM_SCHEMA + " " + INCOMING_TOKEN),
                Arguments.of(HEADER_NAME, "bearer", "Bearer " + INCOMING_TOKEN),
                Arguments.of(HEADER_NAME, CUSTOM_SCHEMA, CUSTOM_SCHEMA + " " + INCOMING_TOKEN));
    }
}
