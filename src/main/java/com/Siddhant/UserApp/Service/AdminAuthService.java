package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.admin.AdminLoginRequest;
import com.Siddhant.UserApp.dto.admin.AdminLoginResponse;
public interface AdminAuthService {
    AdminLoginResponse login(AdminLoginRequest request);
}
