package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.awx_json.api.JobTemplatesApi;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(WiremockAWX.class)
@QuarkusTest
public class AWXTest {

    // injected by quarkus test resource
    WireMockServer awxServer;

    @ConfigProperty(name = WiremockAWX.URL_KEY)
    String awxUrl;

    @RestClient
    @Inject
    JobTemplatesApi jobsApi;

    @Test
    public void verifyAWXApi() {
        jobsApi.jobTemplatesJobTemplatesLaunchCreate("7", null);
        assertNotNull(awxUrl);
        awxServer.verify(postRequestedFor(urlEqualTo("/api/v2/job_templates/7/launch/")));
    }

}
