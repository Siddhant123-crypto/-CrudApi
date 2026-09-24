package com.Siddhant.UserApp.Entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
    @Column(nullable = false)
    private String name;
    @Column(name = "village")
    private String village;
    @Column(name = "address")
    private String address;
    @Column(name = "postal_code")
    private String postalCode;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String mobile;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String state;
    @Column(name = "profile_photo")
    private String profilePhoto;
    @Column(name = "login_count")
    private Integer loginCount = 0;
    private Boolean isDelete;
    private Boolean isActive;
    @Column(name = "inactive_reason")
    private String inactiveReason;
    @Column(name = "inactive_since")
    private LocalDateTime inactiveSince;
    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;
    @Column(name = "block_reason")
    private String blockReason;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime lastLogin;
    
    public void checkAndClearExpiredBlock() {
        if (this.status == Status.INACTIVE && this.blockedUntil != null) {
            java.time.LocalDate currentDate = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Kolkata")).toLocalDate();
            java.time.LocalDate blockedDate = this.blockedUntil.toLocalDate();
            if (!currentDate.isBefore(blockedDate)) {
                this.status = Status.ACTIVE;
                this.isActive = true;
                this.blockedUntil = null;
                this.blockReason = null;
            }
        }
    }
}