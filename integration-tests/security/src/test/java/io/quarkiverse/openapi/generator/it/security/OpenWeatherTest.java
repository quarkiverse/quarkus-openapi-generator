package io.quarkiverse.openapi.generator.it.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.acme.openapi.weather.api.CurrentWeatherDataApi;
import org.acme.openapi.weather.model.Model200;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTestResource(WiremockOpenWeather.class)
@QuarkusTest
public class OpenWeatherTest {

    // injected by quarkus test resource
    WireMockServer openWeatherServer;

    @ConfigProperty(name = "quarkus.openapi-generator.open_weather_yaml.auth.app_id.api-key")
    String apiKey;

    @ConfigProperty(name = WiremockOpenWeather.URL_KEY)
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
                WireMock.urlEqualTo("/data/2.5/weather?q=&id=&lat=10&lon=-10&zip=&units=&lang=&mode=&appid=" + apiKey)));
    }

}
