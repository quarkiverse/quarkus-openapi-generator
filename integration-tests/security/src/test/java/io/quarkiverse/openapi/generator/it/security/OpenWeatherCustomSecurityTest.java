package io.quarkiverse.openapi.generator.it.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.inject.Inject;

import org.acme.openapi.weather.customsecurity.api.CurrentWeatherDataWithCustomSecurityApi;
import org.acme.openapi.weather.customsecurity.model.Model200;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(WiremockOpenWeather.class)
@QuarkusTest
class OpenWeatherCustomSecurityTest {

    // injected by quarkus test resource
    WireMockServer openWeatherServer;

    @ConfigProperty(name = WiremockOpenWeather.URL_CUSTOM_SECURITY_KEY)
    String weatherUrl;

    @RestClient
    @Inject
    CurrentWeatherDataWithCustomSecurityApi currentWeatherDataWithCustomSecurityApi;

    @Test
    void testApiWithCustomSecurity() {
        Model200 model = currentWeatherDataWithCustomSecurityApi.currentWeatherData("", "", "10", "-10", "", "", "", "");
        assertEquals("Nowhere", model.getName());
        assertNotNull(weatherUrl);
        openWeatherServer.verify(WireMock.getRequestedFor(
                WireMock.urlEqualTo("/data/4.5/weather?q=&id=&lat=10&lon=-10&zip=&units=&lang=&mode=&appid=dummyKey")));

    }

}
