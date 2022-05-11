package io.quarkiverse.openapi.generator.providers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;

import java.util.HashMap;
import java.util.Optional;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkiverse.openapi.generator.CodegenConfig;
import io.quarkiverse.openapi.generator.SpecItemAuthConfig;
import io.quarkiverse.openapi.generator.SpecItemAuthsConfig;
import io.quarkiverse.openapi.generator.SpecItemConfig;

@ExtendWith(MockitoExtension.class)
abstract class AbstractAuthenticationProviderTest<T extends AbstractAuthProvider> {

    protected static final String OPEN_API_FILE_SPEC_ID = "open_api_file_spec_id_json";
    protected static final String AUTH_SCHEME_NAME = "auth_scheme_name";

    protected CodegenConfig codegenConfig;

    protected SpecItemAuthConfig specItemAuthConfig;

    @Mock
    protected ClientRequestContext requestContext;

    protected MultivaluedMap<String, Object> headers;

    protected T provider;

    @BeforeEach
    void setUp() {
        createConfiguration();
        provider = createProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, codegenConfig);
    }

    protected abstract T createProvider(String openApiSpecId, String authSchemeName, CodegenConfig codegenConfig);

    protected void createConfiguration() {
        codegenConfig = new CodegenConfig();
        codegenConfig.itemConfigs = new HashMap<>();
        SpecItemConfig specItemConfig = new SpecItemConfig();
        specItemConfig.auth = new SpecItemAuthsConfig();
        specItemConfig.auth.authConfigs = new HashMap<>();
        specItemAuthConfig = new SpecItemAuthConfig();
        specItemAuthConfig.headerName = Optional.empty();
        specItemAuthConfig.tokenPropagation = Optional.of(false);
        specItemAuthConfig.authConfigParams = new HashMap<>();
        specItemConfig.auth.authConfigs.put(AUTH_SCHEME_NAME, specItemAuthConfig);
        codegenConfig.itemConfigs.put(OPEN_API_FILE_SPEC_ID, specItemConfig);
        headers = new MultivaluedHashMap<>();
        lenient().doReturn(headers).when(requestContext).getHeaders();
    }

    protected void assertHeader(MultivaluedMap<String, Object> headers, String headerName, String value) {
        assertThat(headers.getFirst(headerName))
                .isNotNull()
                .isEqualTo(value);
    }
}
