package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Entity
@Table(name = "admin_group_messages")
public class AdminGroupMessage {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(name = "sender_admin_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID senderAdminId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}