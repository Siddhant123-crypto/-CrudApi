package com.Siddhant.UserApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TrackingLocationRequest {
    private Double latitude;
    private Double longitude;
}
