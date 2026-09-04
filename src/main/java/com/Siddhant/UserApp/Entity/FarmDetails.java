package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@Entity
@Table(name = "farm_details")
public class FarmDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private FarmerProfile farmer;
    @Column(name = "farm_name")
    private String farmName;
    @Column(name = "tagline")
    private String tagline;
    @Column(name = "location")
    private String location;
    @Column(name = "farming_since")
    private Integer farmingSince;
    @Column(name = "farming_type")
    private String farmingType;
    @Column(name = "farm_area")
    private Double farmArea;
    @Column(name = "farm_area_unit")
    private String farmAreaUnit;
    @Column(name = "crops_grown_count")
    private Integer cropsGrownCount;
    @Column(name = "years_experience")
    private Integer yearsExperience;
    @Column(name = "happy_customers_count")
    private Integer happyCustomersCount;
    @Column(name = "farmer_name")
    private String farmerName;
    @Column(name = "main_crops")
    private String mainCrops;
    @Column(name = "irrigation_source")
    private String irrigationSource;
    @Column(name = "soil_type")
    private String soilType;
    @Column(name = "about_farm", length = 2000)
    private String aboutFarm;
    @Column(name = "farmer_photo")
    private String farmerPhoto;
    @Column(name = "farm_video")
    private String farmVideo;
    @Column(name = "verified")
    private Boolean verified = false;
    @Column(name = "verified_on")
    private LocalDateTime verifiedOn;
    @Column(name = "certificate_file")
    private String certificateFile;
}
