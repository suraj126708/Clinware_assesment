package com.example.weather;

import com.example.weather.client.WeatherServiceClient;
import com.example.weather.model.WeatherResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ActivityServiceTest {

    @Inject
    ActivityService activityService;

    @InjectMock
    @RestClient
    WeatherServiceClient weatherServiceClient;

    @Test
    public void testBeachRecommendation() {
        // Setup mock response
        WeatherResponse.CurrentWeather weather = new WeatherResponse.CurrentWeather(30.0, 0);
        WeatherResponse response = new WeatherResponse(weather);
        
        Mockito.when(weatherServiceClient.getCurrentWeather(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyBoolean()))
               .thenReturn(response);

        String recommendation = activityService.getRecommendation(0, 0);
        assertTrue(recommendation.contains("beach"), "Should recommend beach for hot clear weather");
    }

    @Test
    public void testMuseumRecommendation() {
        WeatherResponse.CurrentWeather weather = new WeatherResponse.CurrentWeather(10.0, 0);
        WeatherResponse response = new WeatherResponse(weather);
        
        Mockito.when(weatherServiceClient.getCurrentWeather(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyBoolean()))
               .thenReturn(response);

        String recommendation = activityService.getRecommendation(0, 0);
        assertTrue(recommendation.contains("museum"), "Should recommend museum for cold weather");
    }

    @Test
    public void testStayHomeRecommendation() {
        WeatherResponse.CurrentWeather weather = new WeatherResponse.CurrentWeather(20.0, 51);
        WeatherResponse response = new WeatherResponse(weather);
        
        Mockito.when(weatherServiceClient.getCurrentWeather(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyBoolean()))
               .thenReturn(response);

        String recommendation = activityService.getRecommendation(0, 0);
        assertTrue(recommendation.contains("Stay home"), "Should recommend staying home for rain/snow");
    }

    @Test
    public void testAnomalyLogicOddWeather() {
        // Temp 20, Code 1 (Odd). Base = 20*0.8 + 1*0.2 = 16.2 -> (int)16
        // 16 XOR 15 (0x0F) = 31.
        // 31 is prime. Twin prime check: 31-2=29 (prime) OR 31+2=33 (not prime).
        // Since 29 is prime, 31 is a twin prime. Recommendation should be reversed.
        String original = "Go for a hike";
        String result = activityService.applyAnomalyLogic(20.0, 1, original);
        assertEquals(new StringBuilder(original).reverse().toString(), result);
    }

    @Test
    public void testBikeRideRecommendation() {
        assertEquals("Great day for a bike ride!", activityService.recommendActivity(20.0, 2));
        assertEquals("Great day for a bike ride!", activityService.recommendActivity(15.0, 3));
    }

    @Test
    public void testRainRecommendation() {
        assertEquals("Stay indoors and read a book.", activityService.recommendActivity(18.0, 61));
        assertEquals("Stay indoors and read a book.", activityService.recommendActivity(18.0, 80));
    }

    @Test
    public void testSnowRecommendation() {
        assertEquals("Build a snowman!", activityService.recommendActivity(-2.0, 71));
        assertEquals("Build a snowman!", activityService.recommendActivity(-5.0, 85));
    }

    @Test
    public void testThunderstormRecommendation() {
        assertEquals("Stay safe indoors!", activityService.recommendActivity(22.0, 95));
        assertEquals("Stay safe indoors!", activityService.recommendActivity(22.0, 99));
    }

    @Test
    public void testDefaultRecommendation() {
        assertEquals("Check local conditions before heading out.", activityService.recommendActivity(20.0, 10));
    }

    @Test
    public void testComputeAnomalyScoreTwinPrimeOddCode() {
        // baseScore 16.2 rounds to 16; odd code 1 -> 16 ^ 15 = 31 (twin prime)
        String result = activityService.computeAnomalyScore(16.2, 1);
        assertTrue(result.startsWith("Score: 31 | "));
        assertTrue(result.contains(new StringBuilder("Visit the museum.").reverse().toString()));
    }

    @Test
    public void testComputeAnomalyScoreEvenCodeNoXor() {
        String result = activityService.computeAnomalyScore(30.0, 2);
        assertEquals("Score: 30 | Great day for a bike ride!", result);
    }

    @Test
    public void testComputeAnomalyScoreOddNonTwinPrime() {
        // baseScore 17, odd code 1 -> 17 ^ 15 = 30 (not prime)
        String result = activityService.computeAnomalyScore(17.0, 1);
        assertEquals("Score: 30 | Visit the museum.", result);
    }

    @Test
    public void testAnomalyLogicEvenCodeReturnsOriginal() {
        String original = "Great day for a bike ride!";
        assertEquals(original, activityService.applyAnomalyLogic(20.0, 2, original));
    }

    @Test
    public void testAnomalyLogicOddNonTwinPrimeReturnsOriginal() {
        String original = "Stay home and relax.";
        assertEquals(original, activityService.applyAnomalyLogic(20.0, 3, original));
    }

    @Test
    public void testAnomalyLogicTwinPrimeViaPlusTwo() {
        // score = 3 (twin prime with 5 via +2), even weather code
        String original = "Hi";
        assertEquals("iH", activityService.applyAnomalyLogic(3.75, 0, original));
    }

    @Test
    public void testAnomalyLogicCompositeScoreNotTwinPrime() {
        String original = "Hello";
        assertEquals(original, activityService.applyAnomalyLogic(5.0, 0, original));
    }

    @Test
    public void testAnomalyLogicScoreBelowTwoNotTwinPrime() {
        String original = "Low";
        assertEquals(original, activityService.applyAnomalyLogic(1.0, 0, original));
    }
}
