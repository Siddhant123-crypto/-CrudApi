package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Entity
@Table(name = "support_messages")
public class SupportMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "support_message_id", nullable = false, updatable = false)
    private UUID supportMessageId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_request_id", nullable = false)
    private SupportRequest supportRequest;
    @Column(name = "sender_id", nullable = false)
    private UUID senderId;
    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private Role senderType;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "deleted_by_farmer")
    private Boolean deletedByFarmer = false;
    
    @Column(name = "deleted_by_admin")
    private Boolean deletedByAdmin = false;
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
        if (deletedByFarmer == null) {
            deletedByFarmer = false;
        }
        if (deletedByAdmin == null) {
            deletedByAdmin = false;
        }
    }
}
