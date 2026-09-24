package com.Siddhant.UserApp.dto.market;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketRequest {
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
    private String openingTime;
    private String closingTime;
    private String marketContactName;
    private String contactNumber;
    private Boolean isActive;

    }