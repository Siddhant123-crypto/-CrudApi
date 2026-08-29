package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
public class ReviewMediaResponse {
    private UUID mediaId;
    private String mediaType;
    private String mediaUrl;
    private LocalDateTime createdAt;
}
