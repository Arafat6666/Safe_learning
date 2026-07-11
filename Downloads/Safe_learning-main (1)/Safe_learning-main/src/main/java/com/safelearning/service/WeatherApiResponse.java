package com.safelearning.service;

public class WeatherApiResponse {

    private final boolean successful;
    private final String message;
    private final String condition;

    public WeatherApiResponse(boolean successful, String message, String condition) {
        this.successful = successful;
        this.message = message;
        this.condition = condition;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }

    public String getCondition() {
        return condition;
    }
}