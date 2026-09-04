package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.AdminAuthService;
import com.Siddhant.UserApp.dto.admin.AdminLoginRequest;
import com.Siddhant.UserApp.dto.admin.AdminLoginResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/admin")
public class AdminAuthController {
    private final AdminAuthService adminAuthService;
    @Autowired
    public AdminAuthController(AdminAuthService adminAuthService) {this.adminAuthService = adminAuthService;
    } @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminAuthService.login(request);
        if ("Admin login successful".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else if ("Access Denied: Not an Administrator".equals(response.getMessage())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } else if ("Incorrect Password".equals(response.getMessage()) || "Email not found".equals(response.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
