package com.Siddhant.UserApp.dto;
import com.Siddhant.UserApp.Entity.ProductUnit;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductRequest {
    private UUID farmerId;
    private String productName;
    private String category;
    private Double price;
    private Double quantity;
    private ProductUnit unit;
    private String description;
    private Boolean removeVideo;
}