package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID categoryId;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(length = 500)
    private String description;
    @Column(nullable = false)
    private Boolean isActive = true;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    @PrePersist
    protected void onCreate() {createdOn = LocalDateTime.now();updatedOn = LocalDateTime.now();
        if (isActive == null) {isActive = true;}
    }@PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }
}