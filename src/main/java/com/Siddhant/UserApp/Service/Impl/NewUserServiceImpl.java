package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.JwtService;
import com.Siddhant.UserApp.Service.NewUserService;
import com.Siddhant.UserApp.util.GoogleVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.Siddhant.UserApp.dto.NewUserData;
import com.Siddhant.UserApp.dto.NewUserLoginRequest;
import com.Siddhant.UserApp.dto.NewUserLoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NewUserServiceImpl implements NewUserService {

    private static final Logger log = LoggerFactory.getLogger(NewUserServiceImpl.class);

    private final UserRepository userRepository;
    private final GoogleVerifier googleVerifier;
    private final FarmerProfileRepository farmerProfileRepository;
    private final JwtService jwtService;

    @Autowired
    public NewUserServiceImpl(UserRepository userRepository, 
                              GoogleVerifier googleVerifier, 
                              FarmerProfileRepository farmerProfileRepository, 
                              JwtService jwtService) {
        this.userRepository = userRepository;
        this.googleVerifier = googleVerifier;
        this.farmerProfileRepository = farmerProfileRepository;
        this.jwtService = jwtService;
    }

    @Override
    public NewUserLoginResponse login(NewUserLoginRequest request) {
        try {
            log.debug("Attempting login for: {}", request.getEmail());
            
            Optional<User> userOptional = userRepository.findFirstByEmail(request.getEmail());
            if (userOptional.isEmpty()) {
                userOptional = userRepository.findFirstByMobile(request.getEmail());
            }
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (!user.getPassword().equals(request.getPassword())) {
                    return new NewUserLoginResponse("Incorrect Password", null);
                }
                if (!user.getIsActive()) {
                    return new NewUserLoginResponse("Account Inactive", null);
                }
                if (user.getIsDelete()) {
                    return new NewUserLoginResponse("Account Deleted", null);
                }
                
                user.setLoginCount(user.getLoginCount() == null ? 1 : user.getLoginCount() + 1);
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                
                NewUserData data = new NewUserData();
                data.setName(user.getName());
                data.setEmail(user.getEmail());
                data.setRole(user.getRole() == Role.CUSTOMER ? "Customer" : "Farmer");
                data.setProfilePhoto(user.getProfilePhoto());
                
                if (user.getRole() == Role.FARMER) {
                    FarmerProfile farmer = farmerProfileRepository.findByUser(user).orElse(null);
                    if (farmer != null) {
                        data.setId(farmer.getId());
                        data.setFarmerId(farmer.getId());
                    }
                } else {
                    data.setId(user.getUserId());
                    data.setUserId(user.getUserId());
                }
                
                NewUserLoginResponse response = new NewUserLoginResponse("Login Successful", data);
                String token = jwtService.generateToken(data.getEmail(), data.getRole());
                response.setAccessToken(token);
                response.setExpiresIn(86400L);
                
                log.info("{} Login Successful: {}", data.getRole(), data.getEmail());
                return response;
            }
            
            log.warn("Login failed: Account Not Found for {}", request.getEmail());
            return new NewUserLoginResponse("Account Not Found", null);
        } catch (Exception e) {
            log.error("Error during login", e);
            return new NewUserLoginResponse("Error during login: " + e.getMessage(), null);
        }
    }

    @Override
    public NewUserLoginResponse googleLogin(String idToken) {
        try {
            GoogleIdToken token = googleVerifier.verify(idToken);
            if (token == null) {
                return new NewUserLoginResponse("Invalid Google token", null);
            }
            String email = token.getPayload().getEmail();
            log.info("Google Login attempt for email: {}", email);
            
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                userOptional = userRepository.findByMobile(email);
            }
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (!user.getIsActive()) {
                    return new NewUserLoginResponse("Account Inactive", null);
                }
                if (user.getIsDelete()) {
                    return new NewUserLoginResponse("Account Deleted", null);
                }
                
                user.setLoginCount(user.getLoginCount() == null ? 1 : user.getLoginCount() + 1);
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                
                NewUserData data = new NewUserData();
                data.setName(user.getName());
                data.setEmail(user.getEmail());
                data.setRole(user.getRole() == Role.CUSTOMER ? "Customer" : "Farmer");
                data.setProfilePhoto(user.getProfilePhoto());
                
                if (user.getRole() == Role.FARMER) {
                    FarmerProfile farmer = farmerProfileRepository.findByUser(user).orElse(null);
                    if (farmer != null) {
                        data.setId(farmer.getId());
                        data.setFarmerId(farmer.getId());
                    }
                } else {
                    data.setId(user.getUserId());
                    data.setUserId(user.getUserId());
                }
                
                NewUserLoginResponse response = new NewUserLoginResponse("Login Successful", data);
                String jwtToken = jwtService.generateToken(data.getEmail(), data.getRole());
                response.setAccessToken(jwtToken);
                response.setExpiresIn(86400L);
                return response;
            }
            
            log.warn("Account Not Found for Google email: {}", email);
            return new NewUserLoginResponse("Account Not Found", null);
        } catch (Exception e) {
            log.error("Google Login failed", e);
            return new NewUserLoginResponse("Google Login failed: " + e.getMessage(), null);
        }
    }
}