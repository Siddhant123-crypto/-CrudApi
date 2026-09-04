package com.Siddhant.UserApp.dto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data
public class MyFarmResponse {
    private UUID farmerId;
    private String farmerName;
    private String farmName;
    private String farmingType;
    private Double farmSize;
    private String farmSizeUnit;
    private String mainCrops;
    private Integer experienceYears;
    private String soilType;
    private String aboutFarm;
    private Boolean verified;
    private LocalDateTime verifiedOn;
    private String certificateFile;
    private List<String> photos;
}
