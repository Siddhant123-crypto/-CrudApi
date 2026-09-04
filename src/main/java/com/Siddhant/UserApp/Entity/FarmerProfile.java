package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@Entity
@Table(name = "farmer_profile")
public class FarmerProfile {
    public FarmerProfile(int farmerId) {
        this.farmerId = farmerId;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int farmerId;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "farm_name")
    private String farmName;
    @Column(name = "farming_type")
    private String farmingType;
    @Column(name = "farm_size")
    private Double farmSize;
    @Column(name = "farm_size_unit")
    private String farmSizeUnit;
    @Column(name = "main_crops")
    private String mainCrops;
    @Column(name = "experience_years")
    private Integer experienceYears;
    @Column(name = "soil_type")
    private String soilType;
    @Column(name = "farm_video")
    private String farmVideo;
    @Column(name = "about_farm", length = 2000)
    private String aboutFarm;
    @Column(name = "verified")
    private Boolean verified = false;
    @Column(name = "verified_on")
    private LocalDateTime verifiedOn;
    @Column(name = "certificate_file")
    private String certificateFile;
    @Column(name = "verification_message", length = 1000)
    private String verificationMessage;
    @Column(name = "verification_requested_on")
    private LocalDateTime verificationRequestedOn;
}