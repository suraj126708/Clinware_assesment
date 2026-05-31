package com.example.weather;

import com.example.weather.client.WeatherServiceClient;
import com.example.weather.model.WeatherResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ActivityResourceTest {

    @InjectMock
    @RestClient
    WeatherServiceClient weatherServiceClient;

    @Test
    public void testStatusEndpoint() {
        given()
            .when().get("/activity/status")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("service", equalTo("Weather Activity Recommender"))
            .body("timestamp", notNullValue());
    }

    @Test
    public void testGetRecommendationEndpoint() {
        WeatherResponse.CurrentWeather weather = new WeatherResponse.CurrentWeather(30.0, 0);
        WeatherResponse response = new WeatherResponse(weather);

        Mockito.when(weatherServiceClient.getCurrentWeather(
                Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyBoolean()))
            .thenReturn(response);

        given()
            .queryParam("lat", 0.0)
            .queryParam("lon", 0.0)
            .when().get("/activity")
            .then()
            .statusCode(200)
            .body(containsString("beach"));
    }
}
