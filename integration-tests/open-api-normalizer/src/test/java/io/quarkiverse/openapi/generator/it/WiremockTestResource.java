package io.quarkiverse.openapi.generator.it;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

public class WiremockTestResource implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        wireMockServer.stubFor(get(urlMatching("/primate/\\d+"))
                .willReturn(okJson(resourceToString("/primate.json", StandardCharsets.UTF_8))));
        return Collections.singletonMap("org.acme.openapi.animals.api.PrimateApi/mp-rest/url",
                wireMockServer.baseUrl());
    }

    private String resourceToString(String resource, Charset charset) {
        try {
            return IOUtils.resourceToString(resource, charset);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
