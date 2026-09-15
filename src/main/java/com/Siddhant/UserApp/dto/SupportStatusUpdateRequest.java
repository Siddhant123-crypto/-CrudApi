package com.Siddhant.UserApp.dto;

import com.Siddhant.UserApp.enums.SupportRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SupportStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private SupportRequestStatus status;
}
