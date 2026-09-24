package com.Siddhant.UserApp.dto.market;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketResponse {
    private UUID id;
    private String marketName;
    private String description;
    private String address;
    private String village;
    private String taluka;
    private String district;
    private String state;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String marketContactName;
    private String contactNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}