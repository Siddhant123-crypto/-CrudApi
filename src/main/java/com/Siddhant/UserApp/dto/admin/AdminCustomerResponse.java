package com.Siddhant.UserApp.dto.admin;
import com.Siddhant.UserApp.Entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;
@Data
@AllArgsConstructor
public class AdminCustomerResponse {
    private UUID customerId;
    private String name;
    private String email;
    private String mobile;
    private String village;
    private String address;
    private String postalCode;
    private String state;
    private Status status;
    private Boolean isActive;
    private String profilePhoto;
}