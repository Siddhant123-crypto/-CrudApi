package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "farm_gallery")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmGallery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private FarmerProfile farmer;
    @Column(nullable = false)
    private String filePath;
    @Column(nullable = false)
    private String fileType; // IMAGE or VIDEO
    @Column(nullable = false)
    private String originalFileName;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
