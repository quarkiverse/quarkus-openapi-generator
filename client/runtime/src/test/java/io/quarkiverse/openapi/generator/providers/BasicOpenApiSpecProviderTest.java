package io.quarkiverse.openapi.generator.providers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class BasicOpenApiSpecProviderTest extends AbstractOpenApiSpecProviderTest<BasicAuthenticationProvider> {

    private static final String USER = "USER";
    private static final String PASSWORD = "PASSWORD";

    private static final String EXPECTED_BASIC_TOKEN = "Basic "
            + Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes());

    @Override
    protected BasicAuthenticationProvider createProvider(String openApiSpecId, String authSchemeName) {
        return new BasicAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, List.of());
    }

    @Test
    void filter() throws IOException {
        provider.filter(requestContext);
        assertHeader(requestContext.getHeaders(), HttpHeaders.AUTHORIZATION, EXPECTED_BASIC_TOKEN);
    }

    @Test
    void tokenPropagationNotSupported() {
        assertThatThrownBy(
                () -> new BasicAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, List.of()))
                .hasMessageContaining("quarkus.openapi-generator.%s.auth.%s.token-propagation", OPEN_API_FILE_SPEC_ID,
                        AUTH_SCHEME_NAME);
    }
}
