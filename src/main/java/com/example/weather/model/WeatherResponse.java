package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherResponse(
    @JsonProperty("current_weather") CurrentWeather currentWeather
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentWeather(
        double temperature,
        @JsonProperty("weathercode") int weatherCode
    ) {}
}
