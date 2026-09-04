package com.Siddhant.UserApp.dto.admin;
import com.Siddhant.UserApp.Entity.ProductUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;
@Data
@AllArgsConstructor
public class AdminProductResponse {
    private UUID productId;
    private UUID farmerId;
    private String farmerName;
    private String productName;
    private String category;
    private Double price;
    private Double quantity;
    private ProductUnit unit;
    private String description;
    private String productPhoto;
    private String productVideo;
    private Boolean isActive;
    private Boolean isDelete;
    private String status;
    private String harvestDate;
    private Double averageRating;
    private Integer reviewCount;
}