package io.quarkiverse.openapi.generator.providers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import javax.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;

class BasicAuthenticationProviderTest extends AbstractAuthenticationProviderTest<BasicAuthenticationProvider> {

    private static final String USER = "USER";
    private static final String PASSWORD = "PASSWORD";

    private static final String EXPECTED_BASIC_TOKEN = "Basic "
            + Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes());

    @Override
    protected BasicAuthenticationProvider createProvider(String openApiSpecId, String authSchemeName,
            OpenApiGeneratorConfig openApiGeneratorConfig) {
        return new BasicAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, openApiGeneratorConfig);
    }

    @Test
    void filter() throws IOException {
        authConfig.authConfigParams.put(BasicAuthenticationProvider.USER_NAME, USER);
        authConfig.authConfigParams.put(BasicAuthenticationProvider.PASSWORD, PASSWORD);
        provider.filter(requestContext);
        assertHeader(requestContext.getHeaders(), HttpHeaders.AUTHORIZATION, EXPECTED_BASIC_TOKEN);
    }

    @Test
    void tokenPropagationNotSupported() {
        authConfig.tokenPropagation = Optional.of(true);
        assertThatThrownBy(() -> new BasicAuthenticationProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, generatorConfig))
                .hasMessageContaining("quarkus.openapi-generator.%s.auth.%s.token-propagation", OPEN_API_FILE_SPEC_ID,
                        AUTH_SCHEME_NAME);
    }
}
