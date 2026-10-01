package com.Siddhant.UserApp.Entity;
import com.Siddhant.UserApp.enums.SupportRequestStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Entity
@Table(name = "support_requests")
public class SupportRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "support_request_id", nullable = false, updatable = false)
    private UUID supportRequestId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private String subject;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportRequestStatus status = SupportRequestStatus.OPEN;
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.BINARY)
    @Column(name = "assigned_admin_id")
    private UUID assignedAdminId;
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.BINARY)
    @Column(name = "transferred_by_admin_id")
    private UUID transferredByAdminId;
    @Column(name = "has_unread_transfer")
    private Boolean hasUnreadTransfer = false;
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.BINARY)
    @Column(name = "resolved_by")
    private UUID resolvedBy;
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }if (status == null) {
            status = SupportRequestStatus.OPEN;
        }
    }@PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
