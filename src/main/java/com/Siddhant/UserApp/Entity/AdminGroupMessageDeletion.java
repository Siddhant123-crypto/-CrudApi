package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Entity
@Table(name = "admin_group_message_deletions")
public class AdminGroupMessageDeletion {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(name = "message_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID messageId;
    @Column(name = "admin_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID adminId;
    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;
}
