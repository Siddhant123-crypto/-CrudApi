package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.AuthService;
import com.Siddhant.UserApp.dto.LoginRequest;
import com.Siddhant.UserApp.dto.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.dto.LoginData;
import com.Siddhant.UserApp.dto.GoogleLoginRequest;
import com.Siddhant.UserApp.dto.GoogleLoginResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final FarmerProfileRepository farmerProfileRepository;
    private final UserRepository userRepository;
    private final com.Siddhant.UserApp.Service.JwtService jwtService;

    @Value("${google.client-id}")
    private String googleClientId;

    @Autowired
    public AuthServiceImpl(FarmerProfileRepository farmerProfileRepository, 
                           UserRepository userRepository, 
                           com.Siddhant.UserApp.Service.JwtService jwtService) {
        this.farmerProfileRepository = farmerProfileRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            log.debug("Attempting login for: {}", request.getEmail());
            
            var userOptional = request.getEmail().contains("@")
                    ? userRepository.findByEmail(request.getEmail())
                    : userRepository.findByMobile(request.getEmail());
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                log.debug("User found: ID={}, Email={}", user.getUserId(), user.getEmail());
                
                if (!user.getPassword().equals(request.getPassword())) {
                    return new LoginResponse("Incorrect Password", null, null);
                }
                if (!user.getIsActive()) {
                    return new LoginResponse("Account Inactive", null, null);
                }
                if (user.getIsDelete()) {
                    return new LoginResponse("Account Deleted", null, null);
                }
                
                LoginData data = new LoginData();
                data.setName(user.getName());
                data.setEmail(user.getEmail());
                data.setRole(user.getRole());
                
                if (user.getRole() == Role.FARMER) {
                    FarmerProfile farmer = farmerProfileRepository.findByUser(user).orElse(null);
                    if (farmer != null) {
                        data.setFarmerId(farmer.getId());
                    }
                } else {
                    data.setUserId(user.getUserId());
                }
                
                LoginResponse response = new LoginResponse();
                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
                response.setAccessToken(token);
                response.setExpiresIn(86400);
                response.setMessage("Login Successful");
                response.setResponse(data);
                return response;
            }
            
            log.warn("Login failed: Email/Mobile not found for {}", request.getEmail());
            return new LoginResponse("Email not found", null, null);
        } catch (Exception e) {
            log.error("Error during login", e);
            return new LoginResponse("Internal Server Error", null, null);
        }
    }

    @Override
    public com.Siddhant.UserApp.dto.CheckUserResponse checkUser(com.Siddhant.UserApp.dto.CheckUserRequest request) {
        try {
            String identifier = request.getIdentifier();
            boolean isEmail = identifier.contains("@");
            
            var userOptional = isEmail
                    ? userRepository.findByEmail(identifier)
                    : userRepository.findByMobile(identifier);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return new com.Siddhant.UserApp.dto.CheckUserResponse(true, user.getRole() + " found", user.getRole());
            }
            
            return new com.Siddhant.UserApp.dto.CheckUserResponse(false, "User not found", null);
        } catch (Exception e) {
            log.error("Error checking user", e);
            return new com.Siddhant.UserApp.dto.CheckUserResponse(false, "Error: " + e.getMessage(), null);
        }
    }

    @Override
    public GoogleLoginResponse googleLogin(GoogleLoginRequest request) {
        try {
            if (request.getIdToken() == null || request.getIdToken().trim().isEmpty()) {
                return new GoogleLoginResponse("Google ID Token is required", null, null, null);
            }
            
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(googleClientId)).build();
            
            GoogleIdToken googleIdToken = verifier.verify(request.getIdToken());
            if (googleIdToken == null) {
                return new GoogleLoginResponse("Invalid Google ID Token", null, null, null);
            }
            
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            
            log.info("Google Login attempt for email: {}", email);
            
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                LoginData data = new LoginData();
                data.setName(user.getName());
                data.setEmail(user.getEmail());
                data.setRole(user.getRole());
                
                if (user.getRole() == Role.FARMER) {
                    FarmerProfile farmer = farmerProfileRepository.findByUser(user).orElse(null);
                    if (farmer != null) {
                        data.setFarmerId(farmer.getId());
                    }
                } else {
                    data.setUserId(user.getUserId());
                }
                
                String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
                return new GoogleLoginResponse("Login Successful", data, token, 604800L);
            }
            
            log.info("New Google User: {}", email);
            LoginData data = new LoginData();
            data.setName(name);
            data.setEmail(email);
            return new GoogleLoginResponse("NEW_USER", data, null, null);
        } catch (Exception e) {
            log.error("Google Login Failed", e);
            return new GoogleLoginResponse("Google Login Failed: " + e.getMessage(), null, null, null);
        }
    }
}
