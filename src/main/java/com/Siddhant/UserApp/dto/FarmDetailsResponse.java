package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
public class FarmDetailsResponse {
    private UUID id;
    private UUID farmerId;
    private String farmName;
    private String tagline;
    private String location;
    private Integer farmingSince;
    private String farmingType;
    private Double farmArea;
    private String farmAreaUnit;
    private Integer cropsGrownCount;
    private Integer yearsExperience;
    private Integer happyCustomersCount;
    private String farmerName;
    private String mainCrops;
    private String irrigationSource;
    private String soilType;
    private String aboutFarm;
    private String farmerPhoto;
    private String farmVideo;
    private Boolean verified;
    private LocalDateTime verifiedOn;
    private String certificateFile;
}
