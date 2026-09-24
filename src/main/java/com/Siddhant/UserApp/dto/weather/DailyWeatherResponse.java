package com.Siddhant.UserApp.dto.weather;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyWeatherResponse {
    private String date;
    private Double maxTemperature;
    private Double minTemperature;
    private Integer rainProbability;
    private Integer weatherCode;
    private String weatherDescription;
}