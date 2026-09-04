package com.Siddhant.UserApp.dto.admin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class AdminCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
    private String description;
}