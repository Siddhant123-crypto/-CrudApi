package com.Siddhant.UserApp.dto.admin;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
public class AdminFarmVerificationDetailResponse {
    private UUID farmerId;
    private String name;
    private String mobile;
    private String location;
    private String email;
    private String village;
    private String state;
    private String profilePhoto;
    private String farmName;
    private String tagline;
    private String farmingType;
    private Double farmArea;
    private String farmAreaUnit;
    private String mainCrops;
    private Integer experienceYears;
    private Integer farmingSince;
    private Integer cropsGrownCount;
    private Integer happyCustomersCount;
    private String irrigationSource;
    private String soilType;
    private String aboutFarm;
    private String certificateFile;
    private String farmVideo;
    private Boolean verified;
    private LocalDateTime verifiedOn;
    private String verificationMessage; // For rejection reasons
}
