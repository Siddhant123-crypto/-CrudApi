package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
@Data
@NoArgsConstructor
@Entity
@Table(name = "farm_photo")
public class FarmPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private FarmerProfile farmer;
    @Column(name = "file_name", nullable = false)
    private String fileName;
}
