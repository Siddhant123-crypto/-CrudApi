package com.Siddhant.UserApp.dto.weather;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentWeatherResponse {
    private Double temperature;
    private Integer humidity;
    private Integer rainProbability;
    private Double windSpeed;
    private Integer weatherCode;
    private String weatherDescription;
}