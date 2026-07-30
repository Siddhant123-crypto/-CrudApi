package com.Siddhant.UserApp.Service;


import com.Siddhant.UserApp.dto.LoginRequest;
import com.Siddhant.UserApp.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

}
