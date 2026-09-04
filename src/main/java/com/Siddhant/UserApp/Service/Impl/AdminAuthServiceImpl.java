package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Entity.Admin;
import com.Siddhant.UserApp.Repository.AdminRepository;
import com.Siddhant.UserApp.Service.AdminAuthService;
import com.Siddhant.UserApp.Service.JwtService;
import com.Siddhant.UserApp.dto.admin.AdminLoginRequest;
import com.Siddhant.UserApp.dto.admin.AdminLoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {
    private static final Logger log = LoggerFactory.getLogger(AdminAuthServiceImpl.class);
    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminAuthServiceImpl(AdminRepository adminRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        try {
            log.debug("Attempting admin login for: {}", request.getEmail());
            Optional<Admin> adminOptional = adminRepository.findByEmail(request.getEmail());
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();

                if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                    return new AdminLoginResponse("Incorrect Password", null, null, null, null, null);
                }
                if (admin.getIsDelete() != null && admin.getIsDelete()) {
                    return new AdminLoginResponse("Account Deleted", null, null, null, null, null);
                }
                if (admin.getIsActive() != null && !admin.getIsActive()) {
                    return new AdminLoginResponse("Account Inactive", null, null, null, null, null);
                }

                String token = jwtService.generateToken(admin.getEmail(), "ADMIN");
                AdminLoginResponse response = new AdminLoginResponse();
                response.setMessage("Admin login successful");
                response.setToken(token);
                response.setRole("ADMIN");
                response.setAdminId(admin.getId());
                response.setName(admin.getName());
                response.setEmail(admin.getEmail());
                return response;
            }
            log.warn("Admin login failed: Email not found for {}", request.getEmail());
            return new AdminLoginResponse("Email not found", null, null, null, null, null);
        } catch (Exception e) {
            log.error("Error during admin login", e);
            return new AdminLoginResponse("Internal Server Error", null, null, null, null, null);
        }
    }
}
