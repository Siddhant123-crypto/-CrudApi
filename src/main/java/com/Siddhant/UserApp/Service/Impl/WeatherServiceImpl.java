package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Service.WeatherService;
import com.Siddhant.UserApp.dto.weather.CurrentWeatherResponse;
import com.Siddhant.UserApp.dto.weather.DailyWeatherResponse;
import com.Siddhant.UserApp.dto.weather.WeatherResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
@Service
public class WeatherServiceImpl implements WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public WeatherResponse getWeather(Double latitude, Double longitude) {
        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code"
                + "&hourly=precipitation_probability"
                + "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max"
                + "&forecast_days=7"
                + "&timezone=auto";
        try {String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode current = root.get("current");
            JsonNode hourly = root.get("hourly");
            JsonNode daily = root.get("daily");
            Double temperature = current.get("temperature_2m").asDouble();
            Integer humidity = current.get("relative_humidity_2m").asInt();
            Double windSpeed = current.get("wind_speed_10m").asDouble();
            Integer weatherCode = current.get("weather_code").asInt();
            Integer rainProbability = getCurrentRainProbability(
                    hourly,
                    current.get("time").asText()
            );CurrentWeatherResponse currentWeather =
                    new CurrentWeatherResponse(
                            temperature,
                            humidity,
                            rainProbability,
                            windSpeed,
                            weatherCode,
                            getWeatherDescription(weatherCode)
                    );List<DailyWeatherResponse> dailyForecast = new ArrayList<>();
            JsonNode dates = daily.get("time");
            JsonNode maxTemperatures = daily.get("temperature_2m_max");
            JsonNode minTemperatures = daily.get("temperature_2m_min");
            JsonNode rainProbabilities = daily.get("precipitation_probability_max");
            JsonNode weatherCodes = daily.get("weather_code");
            for (int i = 0; i < dates.size(); i++) {
                Integer dailyWeatherCode = weatherCodes.get(i).asInt();
                DailyWeatherResponse weather = new DailyWeatherResponse(
                                dates.get(i).asText(),
                                maxTemperatures.get(i).asDouble(),
                                minTemperatures.get(i).asDouble(),
                                rainProbabilities.get(i).asInt(),
                                dailyWeatherCode,
                                getWeatherDescription(dailyWeatherCode)
                        );dailyForecast.add(weather);
            }return new WeatherResponse(latitude, longitude, currentWeather, dailyForecast
            );
        } catch (Exception e) {throw new RuntimeException("Failed to fetch weather data");
        }
    }private Integer getCurrentRainProbability(JsonNode hourly,
            String currentTime) {

        JsonNode times = hourly.get("time");
        JsonNode probabilities =
                hourly.get("precipitation_probability");

        for (int i = 0; i < times.size(); i++) {
            if (times.get(i).asText().equals(currentTime)) {return probabilities.get(i).asInt();
            }
        }return 0;
    }private String getWeatherDescription(Integer code) {
        switch (code) {
            case 0:
                return "Clear sky";
            case 1:
                return "Mainly clear";
            case 2:
                return "Partly cloudy";
            case 3:
                return "Overcast";
            case 45:
            case 48:
                return "Fog";
            case 51:
            case 53:
            case 55:
                return "Drizzle";
            case 56:
            case 57:
                return "Freezing drizzle";
            case 61:
            case 63:
            case 65:
                return "Rain";

            case 66:
            case 67:
                return "Freezing rain";
            case 71:
            case 73:
            case 75:
                return "Snowfall";
            case 77:
                return "Snow grains";
            case 80:
            case 81:
            case 82:
                return "Rain showers";
            case 85:
            case 86:
                return "Snow showers";
            case 95:
                return "Thunderstorm";
            case 96:
            case 99:
                return "Thunderstorm with hail";
            default:
                return "Unknown";
        }
    }
}