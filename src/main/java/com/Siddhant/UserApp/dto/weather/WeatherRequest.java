package com.Siddhant.UserApp.dto.weather;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherRequest {
    private Double latitude;
    private Double longitude;
}
