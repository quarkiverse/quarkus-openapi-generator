package io.quarkiverse.openapi.generator.providers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

import java.util.Map;
import java.util.Optional;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkiverse.openapi.generator.AuthConfig;
import io.quarkiverse.openapi.generator.AuthsConfig;
import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkiverse.openapi.generator.SpecItemConfig;

@ExtendWith(MockitoExtension.class)
abstract class AbstractAuthenticationProviderTest<T extends AbstractAuthProvider> {

    protected static final String OPEN_API_FILE_SPEC_ID = "open_api_file_spec_id_json";
    protected static final String AUTH_SCHEME_NAME = "auth_scheme_name";

    protected OpenApiGeneratorConfig generatorConfig = new OpenApiGeneratorConfig() {
        @Override
        public Map<String, Optional<SpecItemConfig>> itemConfigs() {
            return Map.of();
        }
    };

    protected AuthConfig authConfig;

    @Mock
    protected ClientRequestContext requestContext;

    protected MultivaluedMap<String, Object> headers;

    protected T provider;

    @BeforeEach
    void setUp() {
        createConfiguration();
        provider = createProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, generatorConfig);
    }

    protected abstract T createProvider(String openApiSpecId, String authSchemeName,
                                        OpenApiGeneratorConfig openApiGeneratorConfig);

    protected void createConfiguration() {
        authConfig = new AuthConfig() {
            @Override
            public Optional<Boolean> tokenPropagation() {
                return Optional.empty();
            }

            @Override
            public Optional<String> headerName() {
                return Optional.empty();
            }

            @Override
            public Map<String, Optional<String>> authConfigParams() {
                return Map.of();
            }
        };

        SpecItemConfig specItemConfig = new SpecItemConfig() {
            @Override
            public Optional<AuthsConfig> auth() {
                return Optional.empty();
            }
        };

        specItemConfig.auth().isPresent().put(AUTH_SCHEME_NAME, authConfig);
        generatorConfig.itemConfigs().put(OPEN_API_FILE_SPEC_ID, specItemConfig);
        headers = new MultivaluedHashMap<>();
        lenient().doReturn(headers).when(requestContext).getHeaders();
    }

    protected void assertHeader(MultivaluedMap<String, Object> headers, String headerName, String value) {
        assertThat(headers.getFirst(headerName))
                .isNotNull()
                .isEqualTo(value);
    }
}
package io.quarkiverse.openapi.generator.providers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

import java.util.Map;
import java.util.Optional;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkiverse.openapi.generator.AuthConfig;
import io.quarkiverse.openapi.generator.AuthsConfig;
import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkiverse.openapi.generator.SpecItemConfig;

@ExtendWith(MockitoExtension.class)
abstract class AbstractAuthenticationProviderTest<T extends AbstractAuthProvider> {

    protected static final String OPEN_API_FILE_SPEC_ID = "open_api_file_spec_id_json";
    protected static final String AUTH_SCHEME_NAME = "auth_scheme_name";

    protected OpenApiGeneratorConfig generatorConfig = new OpenApiGeneratorConfig() {
        @Override
        public Map<String, Optional<SpecItemConfig>> itemConfigs() {
            return Map.of();
        }
    };

    protected AuthConfig authConfig;

    @Mock
    protected ClientRequestContext requestContext;

    protected MultivaluedMap<String, Object> headers;

    protected T provider;

    @BeforeEach
    void setUp() {
        createConfiguration();
        provider = createProvider(OPEN_API_FILE_SPEC_ID, AUTH_SCHEME_NAME, generatorConfig);
    }

    protected abstract T createProvider(String openApiSpecId, String authSchemeName,
                                        OpenApiGeneratorConfig openApiGeneratorConfig);

    protected void createConfiguration() {
        authConfig = new AuthConfig() {
            @Override
            public Optional<Boolean> tokenPropagation() {
                return Optional.empty();
            }

            @Override
            public Optional<String> headerName() {
                return Optional.empty();
            }

            @Override
            public Map<String, Optional<String>> authConfigParams() {
                return Map.of();
            }
        };

        SpecItemConfig specItemConfig = new SpecItemConfig() {
            @Override
            public Optional<AuthsConfig> auth() {
                return Optional.empty();
            }
        };

        specItemConfig.auth().isPresent().put(AUTH_SCHEME_NAME, authConfig);
        generatorConfig.itemConfigs().put(OPEN_API_FILE_SPEC_ID, specItemConfig);
        headers = new MultivaluedHashMap<>();
        lenient().doReturn(headers).when(requestContext).getHeaders();
    }

    protected void assertHeader(MultivaluedMap<String, Object> headers, String headerName, String value) {
        assertThat(headers.getFirst(headerName))
                .isNotNull()
                .isEqualTo(value);
    }
}
