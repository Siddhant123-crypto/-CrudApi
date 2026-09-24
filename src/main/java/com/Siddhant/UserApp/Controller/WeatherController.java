package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.dto.weather.WeatherResponse;
import com.Siddhant.UserApp.Service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;
@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherService weatherService;
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }@GetMapping
    public ResponseEntity<Map<String, Object>> getWeather(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (latitude == null || longitude == null) {
            response.put("message", "Latitude and longitude are required");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }if (latitude < -90 || latitude > 90) {
            response.put("message", "Invalid latitude");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }if (longitude < -180 || longitude > 180) {
            response.put("message", "Invalid longitude");
            response.put("data", null);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }try {WeatherResponse weather =
                    weatherService.getWeather(latitude, longitude);response.put("message", "Weather fetched successfully");response.put("data", weather);
                    return ResponseEntity.ok(response);
        } catch (Exception e) {response.put("message", "Failed to fetch weather");response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}