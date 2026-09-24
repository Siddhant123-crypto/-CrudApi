package com.Siddhant.UserApp.dto.weather;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private Double latitude;
    private Double longitude;
    private CurrentWeatherResponse current;
    private List<DailyWeatherResponse> dailyForecast;
}