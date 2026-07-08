package com.safelearning.service;

public class WeatherApiClient {

    public WeatherApiResponse getWeatherForCampus() {
        return new WeatherApiResponse(
                true,
                "API call successful. Current campus weather condition loaded.",
                "Clear"
        );
    }

    public WeatherApiResponse getFailedApiResponse() {
        return new WeatherApiResponse(
                false,
                "Weather API unavailable. Please continue using the system normally.",
                "Unavailable"
        );
    }

    public WeatherApiResponse getNotFoundResponse() {
        return new WeatherApiResponse(
                false,
                "Weather data is not available for this location.",
                "Not Found"
        );
    }

    public WeatherApiResponse getServerErrorResponse() {
        return new WeatherApiResponse(
                false,
                "External weather service is temporarily unavailable.",
                "Server Error"
        );
    }
}