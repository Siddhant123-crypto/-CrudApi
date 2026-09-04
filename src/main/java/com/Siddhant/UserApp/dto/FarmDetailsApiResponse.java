package com.Siddhant.UserApp.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmDetailsApiResponse {
    private String message;
    private FarmDetailsResponse data;
}
