package com.Siddhant.UserApp.dto.admin;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {
    private String message;
    private AdminData data;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AdminData {
        private String token;
        private String role;
        private UUID adminId;
        private String name;
        private String email;
        private Boolean isActive;
    }
}
