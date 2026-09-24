package com.Siddhant.UserApp.dto.market;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearbyMarketResponse {
    private UUID id;
    private String marketName;
    private String address;
    private String district;
    private String state;
    private Double latitude;
    private Double longitude;
    private Double distance;
    private String distanceUnit;
    private Boolean isActive;
    private java.time.LocalDateTime createdAt;
}