package com.pm.camelintegration.components.rest;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherDataProvider {

    private static Map<String, WeatherDto> weatherData = new HashMap<>();

    public WeatherDataProvider() {
        WeatherDto dto = new WeatherDto();
        dto.setId(1);
        dto.setCity("London");
        dto.setTemp("10");
        dto.setUnit("C");
        dto.setReceivedTime(new Date().toString());
        weatherData.put("LONDON", dto);
    }

    public WeatherDto getCurrentWeather(String city) {
        return weatherData.get(city.toUpperCase());
    }

    public void setCurrentWeather(WeatherDto dto) {
        dto.setReceivedTime(new Date().toString());
        weatherData.put(dto.getCity().toUpperCase(), dto);
    }
}
