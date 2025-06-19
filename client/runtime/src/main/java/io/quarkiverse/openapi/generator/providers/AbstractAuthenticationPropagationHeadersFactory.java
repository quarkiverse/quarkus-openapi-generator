package io.quarkiverse.openapi.generator.providers;

import java.util.List;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;

/**
 * Instances of this class determines which of the authentication related incoming headers must be made available to
 * the filtering phase.
 */
public abstract class AbstractAuthenticationPropagationHeadersFactory implements ClientHeadersFactory {

    private static final String HEADER_NAME_PREFIX_FOR_TOKEN_PROPAGATION = "QCG_%s";
    private static final String HEADER_NAME_FOR_TOKEN_PROPAGATION = "QCG_%s_%s_%s";

    protected BaseCompositeAuthenticationProvider compositeProvider;
    protected OpenApiGeneratorConfig generatorConfig;
    protected HeadersProvider headersProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAuthenticationPropagationHeadersFactory.class);

    protected AbstractAuthenticationPropagationHeadersFactory(BaseCompositeAuthenticationProvider compositeProvider,
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

        LOGGER.debug("Incoming headers keys{}", incomingHeaders.keySet());
        LOGGER.debug("Outgoing headers keys{}", clientOutgoingHeaders.keySet());
        LOGGER.debug("Provided headers keys{}", providedHeaders.keySet());

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
                        // next, look into the incoming headers.
                        headerValue = incomingHeaders.get(headerName);
                    }
                    if (headerValue == null) {
                        // lastly look into the outgoing headers.
                        headerValue = clientOutgoingHeaders.get(headerName);
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
