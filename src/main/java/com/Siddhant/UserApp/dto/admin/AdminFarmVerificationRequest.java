package com.Siddhant.UserApp.dto.admin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class AdminFarmVerificationRequest { @NotBlank(message = "Verification message is required") private String message;}