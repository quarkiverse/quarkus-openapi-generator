package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.Map;

import jakarta.inject.Inject;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(EnumUnexpectedTest.EchoMockServer.class)
class EnumUnexpectedTest {

    @RestClient
    @Inject
    org.openapi.quarkus.with_enum_unexpected_yaml.api.DefaultApi api;
    @Inject
    ObjectMapper objectMapper;

    @Test
    void apiIsBeingGenerated() {

        org.openapi.quarkus.with_enum_unexpected_yaml.model.Echo echo = api.echo();

        Assertions.assertThat(echo.getMsgType())
                .isEqualTo(org.openapi.quarkus.with_enum_unexpected_yaml.model.Echo.MsgTypeEnum.UNEXPECTED);
    }

    @Test
    void when_additional_enum_type_unexpected_member_is_true_should_have_extra_member() {
        AssertionsForClassTypes.assertThat(org.openapi.quarkus.with_enum_unexpected_yaml.model.Echo.MsgTypeEnum.class)
                .hasOnlyPublicFields("TEXT", "UNEXPECTED");
    }

    @Test
    void when_additional_enum_type_unexpected_is_false_should_not_have_extra_member() {
        AssertionsForClassTypes.assertThat(org.openapi.quarkus.without_enum_unexpected_yaml.model.Echo.MsgTypeEnum.class)
                .hasOnlyPublicFields("TEXT");
    }

    @Test
    void when_additional_enum_type_unexpected_member_is_true_should_parse_unknown_values_to_UNEXPECTED()
            throws JsonProcessingException {
        org.openapi.quarkus.with_enum_unexpected_yaml.model.Echo actualEcho = objectMapper.readValue("{ \"msgType\": \"NOPE\"}",
                org.openapi.quarkus.with_enum_unexpected_yaml.model.Echo.class);
        MatcherAssert.assertThat(actualEcho.getMsgType(),
                is(org.openapi.quarkus.with_enum_unexpected_yaml.model.Echo.MsgTypeEnum.UNEXPECTED));
    }

    @Test
    void when_additional_enum_type_unexpected_member_is_false_should_fail_parsing_unknown_values() {
        assertThatThrownBy(() -> {
            objectMapper.readValue("{ \"msgType\": \"NOPE\"}",
                    org.openapi.quarkus.without_enum_unexpected_yaml.model.Echo.class);
        }).isInstanceOf(ValueInstantiationException.class);
    }

    public static class EchoMockServer implements QuarkusTestResourceLifecycleManager {

        private WireMockServer wireMockServer;

        @Override
        public Map<String, String> start() {
            configureWiremockServer();
            return Collections.singletonMap("org.openapi.quarkus.with_enum_unexpected_yaml.api.DefaultApi/mp-rest/url",
                    wireMockServer.baseUrl());
        }

        private void configureWiremockServer() {
            var wireMockConfiguration = WireMockConfiguration.wireMockConfig().dynamicPort();
            wireMockServer = new WireMockServer(wireMockConfiguration);
            wireMockServer.start();

            wireMockServer.stubFor(post(urlEqualTo("/echo"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{ \"msgType\": \"NOPE\"}")
                            .withTransformers("response-template")));
        }

        @Override
        public void stop() {
            if (wireMockServer != null) {
                wireMockServer.stop();
            }
        }
    }
}
