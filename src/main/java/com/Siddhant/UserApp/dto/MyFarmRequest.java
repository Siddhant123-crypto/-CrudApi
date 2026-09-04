package com.Siddhant.UserApp.dto;
import lombok.Data;
@Data
public class MyFarmRequest {
    private String farmName;
    private String farmingType;
    private Double farmSize;
    private String farmSizeUnit;
    private String mainCrops;
    private Integer experienceYears;
    private String soilType;
    private String aboutFarm;
}
