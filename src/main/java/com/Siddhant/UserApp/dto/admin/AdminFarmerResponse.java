package com.Siddhant.UserApp.dto.admin;
import com.Siddhant.UserApp.Entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminFarmerResponse {
    private UUID farmerId;
    private String name;
    private String email;
    private String mobile;
    private String village;
    private String address;
    private String postalCode;
    private String state;
    private Status status;
    private Boolean isActive;
    private String farmName;
    private String farmingType;
    private Double farmSize;
    private String farmSizeUnit;
    private String mainCrops;
    private Integer experienceYears;
    private String soilType;
    private String farmVideo;
    private String aboutFarm;
    private Boolean verified;
    private String certificateFile;
    private String profilePhoto;
}