package com.Siddhant.UserApp.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class ProductUpdateResponse {
    private String message;
    private ProductResponse response;
    public ProductUpdateResponse(String message, ProductResponse response
    ) {this.message = message;
        this.response = response;
    }
}