package com.Siddhant.UserApp.dto;
import com.Siddhant.UserApp.Entity.ProductUnit;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
public class ProductResponse {
    private UUID productId;
    private UUID farmerId;
    private String farmerName;
    private String farmerAddress;
    private String farmerState;
    private String farmerVillage;
    private String productName;
    private String category;
    private Double price;
    private Double quantity;
    private ProductUnit unit;
    private String description;
    private String productPhoto;
    // Optional product video
    private String productVideo;
    private Boolean isActive;
    private Boolean isDelete;
    private String status;
    private String createdBy;
    private LocalDateTime createdOn;
    private String updatedBy;
    private LocalDateTime updatedOn;
}