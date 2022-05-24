package io.quarkiverse.openapi.generator.providers;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;

/**
 * Instances of this class determines which of the authentication related incoming headers must be made available to
 * the filtering phase.
 */
public abstract class AbstractAuthenticationPropagationHeadersFactory implements ClientHeadersFactory {

    private static final String HEADER_NAME_PREFIX_FOR_TOKEN_PROPAGATION = "QCG_%s";
    private static final String HEADER_NAME_FOR_TOKEN_PROPAGATION = "QCG_%s_%s_%s";

    protected AbstractCompositeAuthenticationProvider compositeProvider;
    protected OpenApiGeneratorConfig generatorConfig;
    protected HeadersProvider headersProvider;

    protected AbstractAuthenticationPropagationHeadersFactory(AbstractCompositeAuthenticationProvider compositeProvider,
            OpenApiGeneratorConfig generatorConfig,
            HeadersProvider headersProvider) {
        this.compositeProvider = compositeProvider;
        this.generatorConfig = generatorConfig;
        this.headersProvider = headersProvider;
    }

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders) {
        MultivaluedMap<String, String> propagatedHeaders = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> providedHeaders = headersProvider.getStringHeaders(generatorConfig);
        compositeProvider.getAuthenticationProviders().stream()
                .filter(AbstractAuthProvider.class::isInstance)
                .map(AbstractAuthProvider.class::cast)
                .filter(AbstractAuthProvider::isTokenPropagation)
                .forEach(authProvider -> {
                    String headerName = authProvider.getHeaderName() != null ? authProvider.getHeaderName()
                            : HttpHeaders.AUTHORIZATION;
                    // get priority to the headers coming from the headers provider.
                    List<String> headerValue = providedHeaders.get(headerName);
                    if (headerValue == null) {
                        // lastly look into the incoming headers.
                        headerValue = incomingHeaders.get(headerName);
                    }
                    if (headerValue != null) {
                        propagatedHeaders.put(propagationHeaderName(authProvider.getOpenApiSpecId(),
                                authProvider.getName(),
                                headerName),
                                headerValue);
                    }
                });
        return propagatedHeaders;
    }

    public static String propagationHeaderName(String openApiSpecId, String authName, String headerName) {
        return String.format(HEADER_NAME_FOR_TOKEN_PROPAGATION, openApiSpecId, authName, headerName);
    }

    public static String propagationHeaderNamePrefix(String openApiSpecId) {
        return String.format(HEADER_NAME_PREFIX_FOR_TOKEN_PROPAGATION, openApiSpecId);
    }
}
