package io.quarkiverse.openapi.generator.it;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;

import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.openapi.quarkus.enum_property_yaml.api.DefaultApi;
import org.openapi.quarkus.enum_property_yaml.model.Echo;
import org.openapi.quarkus.enum_property_yaml.model.Message;
import org.openapi.quarkus.enum_property_yaml.model.MessageInt;
import org.openapi.quarkus.enum_property_yaml.model.MessageNum;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(EnumPropertyTest.EchoMockServer.class)
class EnumPropertyTest {

    @RestClient
    @Inject
    DefaultApi api;

    @Test
    void apiIsBeingGenerated() {
        var message = new Message();
        message.setMsgType(Message.MsgTypeEnum.TEXT);
        var messageNum = new MessageNum();
        messageNum.setMsgType(MessageNum.MsgTypeEnum.NUMBER_1_DOT_1);
        var messageInt = new MessageInt();
        messageInt.setMsgType(MessageInt.MsgTypeEnum.NUMBER_2);

        Echo echo = api.echo(message);
        Echo echoNum = api.echoNum(messageNum);
        Echo echoInt = api.echoInt(messageInt);

        assertThat(echo.getEchoedMsgType())
                .isEqualTo("text");
        assertThat(echoNum.getEchoedMsgType())
                .isEqualTo("1.1");
        assertThat(echoInt.getEchoedMsgType())
                .isEqualTo("2");
    }

    public static class EchoMockServer implements QuarkusTestResourceLifecycleManager {

        private WireMockServer wireMockServer;

        @Override
        public Map<String, String> start() {
            configureWiremockServer();
            return Collections.singletonMap("org.openapi.quarkus.enum_property_yaml.api.DefaultApi/mp-rest/url",
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
                            .withBody("{ \"echoedMsgType\": \"{{jsonPath request.body '$.msgType'}}\"}")
                            .withTransformers("response-template")));

            wireMockServer.stubFor(post(urlEqualTo("/echo/num"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{ \"echoedMsgType\": \"{{jsonPath request.body '$.msgType'}}\"}")
                            .withTransformers("response-template")));

            wireMockServer.stubFor(post(urlEqualTo("/echo/int"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{ \"echoedMsgType\": \"{{jsonPath request.body '$.msgType'}}\"}")
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
