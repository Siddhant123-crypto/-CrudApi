package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "market")
public class Market {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(nullable = false)
    private String marketName;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String address;
    private String village;
    private String taluka;
    private String district;
    private String state;
    private String pincode;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String marketContactName;
    private String contactNumber;
    @Column(nullable = false)
    private Boolean isActive = true;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }@PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}