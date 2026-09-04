package com.Siddhant.UserApp.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@AllArgsConstructor
public class AdminCategoryResponse {
    private UUID categoryId;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}