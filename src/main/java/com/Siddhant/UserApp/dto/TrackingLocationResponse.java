package com.Siddhant.UserApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TrackingLocationResponse {
    private String farmerName;
    private String farmName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime lastUpdated;

    public TrackingLocationResponse(String farmerName, String farmName, Double latitude, Double longitude, LocalDateTime lastUpdated) {
        this.farmerName = farmerName;
        this.farmName = farmName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastUpdated = lastUpdated;
    }
}
