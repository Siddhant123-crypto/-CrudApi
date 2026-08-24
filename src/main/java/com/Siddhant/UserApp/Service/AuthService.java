package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.*;
public interface AuthService {
    LoginResponse login(LoginRequest request);
    CheckUserResponse checkUser(CheckUserRequest request);
    GoogleLoginResponse googleLogin(GoogleLoginRequest request);
}
