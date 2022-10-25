package io.quarkiverse.openapi.generator.it.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.acme.openapi.weathernosec.api.CurrentWeatherDataApi;
import org.acme.openapi.weathernosec.model.Model200;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(WiremockOpenWeather.class)
@QuarkusTest
public class OpenWeatherDefaultSecurityTest {

    // injected by quarkus test resource
    WireMockServer openWeatherServer;

    @ConfigProperty(name = WiremockOpenWeather.URL_NO_SEC_KEY)
    String weatherUrl;

    @RestClient
    @Inject
    CurrentWeatherDataApi weatherApi;

    @Test
    public void testGetWeatherByLatLon() {
        final Model200 model = weatherApi.currentWeatherData("", "", "10", "-10", "", "", "", "");
        assertEquals("Nowhere", model.getName());
        assertNotNull(weatherUrl);
        openWeatherServer.verify(WireMock.getRequestedFor(
                WireMock.urlEqualTo("/data/2.5/weather?q=&id=&lat=10&lon=-10&zip=&units=&lang=&mode=")));
    }

}
