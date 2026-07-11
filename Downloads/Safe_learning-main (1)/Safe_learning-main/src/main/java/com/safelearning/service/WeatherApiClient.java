package com.safelearning.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherApiClient {

    private static final String CAMPUS_WEATHER_ENDPOINT =
            "https://api.open-meteo.com/v1/forecast?latitude=3.0649&longitude=101.6168&current_weather=true";

    private final HttpClient httpClient;

    public WeatherApiClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public WeatherApiResponse getWeatherForCampus() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CAMPUS_WEATHER_ENDPOINT))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return handleResponse(response.statusCode(), response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return new WeatherApiResponse(
                    false,
                    "External weather information is currently unavailable. Please continue using the system normally.",
                    "Unavailable"
            );
        }
    }

    public WeatherApiResponse handleResponse(int statusCode, String responseBody) {
        if (statusCode >= 200 && statusCode < 300) {
            return parseSuccessfulResponse(responseBody);
        }

        if (statusCode == 404) {
            return getNotFoundResponse();
        }

        if (statusCode >= 500) {
            return getServerErrorResponse();
        }

        return getFailedApiResponse();
    }

    private WeatherApiResponse parseSuccessfulResponse(String responseBody) {
        String temperature = extractValue(responseBody, "\"temperature\":([-0-9.]+)");
        String weatherCode = extractValue(responseBody, "\"weathercode\":([0-9]+)");

        String condition = mapWeatherCode(weatherCode);
        String message = "API call successful. Campus weather: " + condition
                + ", temperature: " + temperature + "°C.";

        return new WeatherApiResponse(true, message, condition);
    }

    private String extractValue(String json, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "Unknown";
    }

    private String mapWeatherCode(String code) {
        return switch (code) {
            case "0" -> "Clear";
            case "1", "2", "3" -> "Partly Cloudy";
            case "45", "48" -> "Fog";
            case "51", "53", "55", "61", "63", "65" -> "Rain";
            case "80", "81", "82" -> "Rain Shower";
            case "95", "96", "99" -> "Thunderstorm";
            default -> "Unknown";
        };
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