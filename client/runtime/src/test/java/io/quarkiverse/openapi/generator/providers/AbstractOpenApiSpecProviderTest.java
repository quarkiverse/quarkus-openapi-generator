package io.quarkiverse.openapi.generator.providers;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class AbstractOpenApiSpecProviderTest<T extends AbstractAuthProvider> {

    protected static final String OPEN_API_FILE_SPEC_ID = "open_api_file_spec_id_json";
    protected static final String AUTH_SCHEME_NAME = "auth_scheme_name";
    protected static final String QUARKUS_CONFIG_KEY = "quarkus.openapi-generator." + OPEN_API_FILE_SPEC_ID + ".auth."
            + AUTH_SCHEME_NAME;

    @Mock
    protected ClientRequestContext requestContext;
    protected MultivaluedMap<String, Object> headers;

    protected T provider;

    @BeforeEach
    void setUp() {
        headers = new MultivaluedHashMap<>();
        Mockito.lenient().doReturn(headers).when(requestContext).getHeaders();
        provider = createProvider();
    }

    protected abstract T createProvider();

    protected void assertHeader(MultivaluedMap<String, Object> headers, String headerName, String value) {
        Assertions.assertThat(headers.getFirst(headerName))
                .isNotNull()
                .isEqualTo(value);
    }
}
