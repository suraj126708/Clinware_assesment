package com.example.weather.client;

import com.example.weather.model.WeatherResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * REST Client for Open-Meteo API.
 * Documentation: https://open-meteo.com/en/docs
 */
@ApplicationScoped
@RegisterRestClient
public interface WeatherServiceClient {

    @GET
    @Path("/v1/forecast")
    WeatherResponse getCurrentWeather(
        @QueryParam("latitude") double latitude,
        @QueryParam("longitude") double longitude,
        @QueryParam("current_weather") boolean currentWeather
    );
}
