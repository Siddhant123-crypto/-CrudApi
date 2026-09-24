package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.weather.WeatherResponse;
public interface WeatherService {
    WeatherResponse getWeather(Double latitude, Double longitude);
}