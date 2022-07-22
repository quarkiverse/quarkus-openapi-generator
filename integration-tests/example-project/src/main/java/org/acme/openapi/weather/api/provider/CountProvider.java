package org.acme.openapi.weather.api.provider;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountProvider implements ClientRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(CountProvider.class);
    private static final Map<String, Integer> REQUEST_COUNT = new ConcurrentHashMap<>();

    @Override
    public void filter(ClientRequestContext context) throws IOException {
        String operationName = String.join(":", context.getMethod(), context.getUri().toString());
        REQUEST_COUNT.computeIfPresent(operationName, (op, count) -> count + 1);
        log.info("Request {} for {} times", operationName, REQUEST_COUNT.get(operationName));
    }

    public Map<String, Integer> requestCounts() {
        return Collections.unmodifiableMap(REQUEST_COUNT);
    }
}
