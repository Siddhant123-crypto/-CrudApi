package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;
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
}