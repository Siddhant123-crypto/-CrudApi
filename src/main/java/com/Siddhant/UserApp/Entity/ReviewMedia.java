package com.Siddhant.UserApp.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@Entity
@Table(name = "review_media")
public class ReviewMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID mediaId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    @JsonIgnore
    private Review review;
    @Column(nullable = false)
    private String mediaType; // "IMAGE" or "VIDEO"
    @Column(nullable = false, length = 1000)
    private String mediaUrl;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
