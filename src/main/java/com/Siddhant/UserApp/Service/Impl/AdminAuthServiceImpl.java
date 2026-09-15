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
    public AdminLoginResponse register(com.Siddhant.UserApp.dto.admin.AdminRegisterRequest request) {
        try {
            log.debug("Attempting to register new admin: {}", request.getEmail());
            if (adminRepository.findByEmail(request.getEmail().trim()).isPresent()) {
                return new AdminLoginResponse("Email already exists", null);
            }

            Admin admin = new Admin();
            admin.setName(request.getName().trim());
            admin.setEmail(request.getEmail().trim());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setIsActive(true);
            admin.setIsDelete(false);

            adminRepository.save(admin);
            
            AdminLoginResponse.AdminData data = new AdminLoginResponse.AdminData(
                    null, 
                    "ADMIN", 
                    admin.getId(), 
                    admin.getName(), 
                    admin.getEmail(), 
                    admin.getIsActive()
            );
            
            return new AdminLoginResponse("Admin registered successfully", data);
        } catch (Exception e) {
            log.error("Error during admin registration", e);
            return new AdminLoginResponse("Internal Server Error", null);
        }
    }

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        try {
            log.debug("Attempting admin login for: {}", request.getEmail());
            Optional<Admin> adminOptional = adminRepository.findByEmail(request.getEmail().trim());
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();

                if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                    return new AdminLoginResponse("Incorrect Password", null);
                }
                if (admin.getIsDelete() != null && admin.getIsDelete()) {
                    return new AdminLoginResponse("Account Deleted", null);
                }
                if (admin.getIsActive() != null && !admin.getIsActive()) {
                    return new AdminLoginResponse("Account Inactive", null);
                }

                String token = jwtService.generateToken(admin.getEmail(), "ADMIN");
                AdminLoginResponse response = new AdminLoginResponse();
                response.setMessage("Admin login successful");
                response.setData(new AdminLoginResponse.AdminData(token, "ADMIN", admin.getId(), admin.getName(), admin.getEmail(), admin.getIsActive()));
                return response;
            }
            log.warn("Admin login failed: Email not found for {}", request.getEmail());
            return new AdminLoginResponse("Email not found", null);
        } catch (Exception e) {
            log.error("Error during admin login", e);
            return new AdminLoginResponse("Internal Server Error", null);
        }
    }
}
