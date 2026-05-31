package com.example.weather;

import com.example.weather.client.WeatherServiceClient;
import com.example.weather.model.WeatherResponse;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ActivityService {

    @RestClient
    WeatherServiceClient weatherClient;

    public String getRecommendation(double lat, double lon) {
        WeatherResponse response = weatherClient.getCurrentWeather(lat, lon, true);
        var current = response.currentWeather();
        return recommendActivity(current.temperature(), current.weatherCode());
    }

    public String recommendActivity(double temperature, int weatherCode) {
        if (weatherCode >= 0 && weatherCode <= 1) {
            return temperature >= 25 ? "Head to the beach!" : "Visit the museum.";
        }
        if (weatherCode >= 2 && weatherCode <= 3) {
            return "Great day for a bike ride!";
        }
        if (weatherCode >= 45 && weatherCode <= 57) {
            return "Stay home and relax.";
        }
        if ((weatherCode >= 61 && weatherCode <= 67) || (weatherCode >= 80 && weatherCode <= 82)) {
            return "Stay indoors and read a book.";
        }
        if ((weatherCode >= 71 && weatherCode <= 77) || (weatherCode >= 85 && weatherCode <= 86)) {
            return "Build a snowman!";
        }
        if (weatherCode >= 95 && weatherCode <= 99) {
            return "Stay safe indoors!";
        }
        return "Check local conditions before heading out.";
    }

    public String computeAnomalyScore(double baseScore, int weatherCode) {
        int score = (int) Math.round(baseScore);
        if (weatherCode % 2 != 0) {
            score = score ^ 0x0F;
        }

        String recommendation = recommendActivity(baseScore, weatherCode);

        if (isTwinPrime(score)) {
            recommendation = new StringBuilder(recommendation).reverse().toString();
        }

        return "Score: " + score + " | " + recommendation;
    }

    public String applyAnomalyLogic(double temp, int weatherCode, String recommendation) {
        double baseScore = temp * 0.8 + weatherCode * 0.2;
        int score = (int) Math.round(baseScore);
        if (weatherCode % 2 != 0) {
            score = score ^ 0x0F;
        }
        if (isTwinPrime(score)) {
            return new StringBuilder(recommendation).reverse().toString();
        }
        return recommendation;
    }

    private boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isTwinPrime(int score) {
        return isPrime(score) && (isPrime(score - 2) || isPrime(score + 2));
    }
}
