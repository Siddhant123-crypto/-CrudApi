package com.Siddhant.UserApp.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmGalleryResponse {
    private UUID id;
    private UUID farmerId;
    private String filePath;
    private String fileType;
    private String originalFileName;
    private LocalDateTime createdAt;
}
