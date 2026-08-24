package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.AuthService;
import com.Siddhant.UserApp.dto.GoogleLoginRequest;
import com.Siddhant.UserApp.dto.GoogleLoginResponse;
import com.Siddhant.UserApp.dto.LoginRequest;
import com.Siddhant.UserApp.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {
    private final AuthService authService;
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
    @PostMapping("/check-user")
    public com.Siddhant.UserApp.dto.CheckUserResponse checkUser(
            @jakarta.validation.Valid @RequestBody com.Siddhant.UserApp.dto.CheckUserRequest request) {
        return authService.checkUser(request);
    }
    @PostMapping("/google")
    public GoogleLoginResponse googleLogin(@RequestBody GoogleLoginRequest request) {
        return authService.googleLogin(request);
    }
}