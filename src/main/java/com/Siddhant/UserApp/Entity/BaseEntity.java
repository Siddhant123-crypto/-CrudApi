package com.Siddhant.UserApp.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Data;
@Data
@MappedSuperclass
public class BaseEntity {
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "is_delete")
    private Boolean isDelete;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
