package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private FarmerProfile farmer;
    @Column(nullable = false)
    private String productName;
    private String category;
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false)
    private Double quantity;
    @Enumerated(EnumType.STRING)
    private ProductUnit unit;
    @Column(length = 500)
    private String description;
    private String productPhoto;
    // Optional
    private String productVideo;
    private String createdBy;
    private LocalDateTime createdOn;
    private String updatedBy;
    private LocalDateTime updatedOn;
    private Boolean isActive;
    private Boolean isDelete;
    private String status;
    private String harvestDate;
    private Double averageRating = 0.0;
    private Integer reviewCount = 0;
}