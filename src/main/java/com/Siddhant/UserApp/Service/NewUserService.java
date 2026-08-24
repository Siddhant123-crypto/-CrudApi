package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.NewUserLoginRequest;
import com.Siddhant.UserApp.dto.NewUserLoginResponse;
public interface NewUserService {
    NewUserLoginResponse login(NewUserLoginRequest request);
    NewUserLoginResponse googleLogin(String idToken);
}
