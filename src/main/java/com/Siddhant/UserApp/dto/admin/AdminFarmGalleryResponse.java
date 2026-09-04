package com.Siddhant.UserApp.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@AllArgsConstructor
public class AdminFarmGalleryResponse {
    private UUID id;
    private UUID farmerId;
    private String filePath;
    private String fileType;
    private String originalFileName;
    private LocalDateTime createdAt;
}